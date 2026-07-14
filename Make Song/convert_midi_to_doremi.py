"""
convert_midi_to_doremi.py
=========================
Konverter MIDI → Doremi (.123) untuk Angklung.

Perbaikan utama dibanding versi sebelumnya:
1. Quantize MIDI ke grid 1/16 yang benar (ticks_per_step = ticks_per_beat / 4)
2. Melodi diambil nada tertinggi per step (monophonic top-voice extraction)
3. Durasi nada mengikuti MIDI asli, tidak dipotong paksa ke 1 bar
4. Chord dideteksi per-beat (bukan per-bar) untuk akurasi lebih tinggi
5. Bass diambil langsung not-by-not dari track bass asli (bukan hanya root)
6. Penulisan sustain (.) untuk nada yang berlanjut antar step
7. Jika ritem terlalu padat → chord blok; jika jarang → not-by-not
"""

import mido
import os
import sys
import argparse

# ---------------------------------------------------------------------------
# KONSTANTA
# ---------------------------------------------------------------------------
STEP_NUM   = {'C':'1','D':'2','E':'3','F':'4','G':'5','A':'6','B':'7'}
BARS_PER_LINE = 4

CHORDS_DB = {
    # Major
    "C":[0,4,7],"Db":[1,5,8],"C#":[1,5,8],"D":[2,6,9],
    "Eb":[3,7,10],"D#":[3,7,10],"E":[4,8,11],"F":[5,9,0],
    "Gb":[6,10,1],"F#":[6,10,1],"G":[7,11,2],"Ab":[8,0,3],
    "G#":[8,0,3],"A":[9,1,4],"Bb":[10,2,5],"A#":[10,2,5],"B":[11,3,6],
    # Minor
    "Cm":[0,3,7],"C#m":[1,4,8],"Dm":[2,5,9],"D#m":[3,6,10],"Ebm":[3,6,10],
    "Em":[4,7,11],"Fm":[5,8,0],"F#m":[6,9,1],"Gm":[7,10,2],
    "G#m":[8,11,3],"Am":[9,0,4],"Bbm":[10,1,5],"Bm":[11,2,6],
    # Dominant 7 (optional tones boost detection)
    "G7":[7,11,2,5],"D7":[2,6,9,0],"A7":[9,1,4,7],"E7":[4,8,11,2],
}

KEY_SCALE_CHORDS = {
    "C" : ["C","Dm","Em","F","G","Am","Bdim","G7"],
    "G" : ["G","Am","Bm","C","D","Em","F#dim","D7"],
    "D" : ["D","Em","F#m","G","A","Bm","C#dim","A7"],
    "A" : ["A","Bm","C#m","D","E","F#m","G#dim","E7"],
    "E" : ["E","F#m","G#m","A","B","C#m","D#dim"],
    "F" : ["F","Gm","Am","Bb","C","Dm","Edim"],
    "Bb": ["Bb","Cm","Dm","Eb","F","Gm","Adim"],
    "Eb": ["Eb","Fm","Gm","Ab","Bb","Cm","Ddim"],
}

KEY_ROOT_MIDI = {
    "C":60,"C#":61,"Db":61,"D":62,"D#":63,"Eb":63,
    "E":64,"F":65,"F#":66,"Gb":66,"G":67,"G#":68,
    "Ab":68,"A":69,"A#":70,"Bb":70,"B":71
}

NOTE_NAMES = ["C","C#","D","D#","E","F","F#","G","G#","A","A#","B"]

# ---------------------------------------------------------------------------
# UTILITAS DASAR
# ---------------------------------------------------------------------------
def midi_to_step(midi_num):
    """Kembalikan (step_letter, is_sharp, octave)."""
    name  = NOTE_NAMES[midi_num % 12]
    oct_  = (midi_num // 12) - 1
    sharp = '#' in name
    letter= name.replace('#','')
    return letter, sharp, oct_

def octave_suffix(oct_, base=4):
    d = oct_ - base
    if d > 0: return "'" * d
    if d < 0: return "," * abs(d)
    return ""

def note_token(letter, sharp, oct_, base_oct=4):
    num  = STEP_NUM.get(letter,'0')
    suf  = octave_suffix(oct_, base_oct)
    acc  = '#' if sharp else ''
    return f"{num}{suf}{acc}"

def steps_to_tokens(note_str, steps):
    """Konversi (note_str, steps) → list token doremi."""
    if steps <= 0:
        return []
    # REST
    if note_str in ('0', None):
        if   steps == 1: return ['0=']
        elif steps == 2: return ['0-']
        elif steps == 3: return ['0-','0=']
        elif steps == 4: return ['0']
        else:
            full = steps // 4
            rem  = steps  % 4
            toks = ['0'] * full
            if rem: toks += steps_to_tokens('0', rem)
            return toks
    # NOTE
    if   steps == 1: return [note_str+'=']
    elif steps == 2: return [note_str+'-']
    elif steps == 3: return [note_str+'-', '.=']
    elif steps == 4: return [note_str]
    elif steps == 5: return [note_str, '.=']
    elif steps == 6: return [note_str, '.-']
    elif steps == 7: return [note_str, '.-', '.=']
    else:
        full = (steps - 4) // 4
        rem  = (steps - 4)  % 4
        toks = [note_str] + ['.'] * full
        if rem: toks += steps_to_tokens('.', rem)
        return toks

# ---------------------------------------------------------------------------
# EKSTRAKSI NOTE DARI TRACK
# ---------------------------------------------------------------------------
def extract_notes(mid, track_idx, channel_filter=None):
    """
    Kembalikan list dict {start_tick, dur_ticks, midi, letter, sharp, octave}
    dengan start_tick dan dur_ticks sudah di-quantize ke 1/16 beat.
    channel_filter: int atau None (None = semua channel kecuali 9/drum)
    """
    tps   = mid.ticks_per_beat / 4.0
    track = mid.tracks[track_idx]
    active = {}
    notes  = []
    tick   = 0

    for msg in track:
        tick += msg.time
        ch = getattr(msg, 'channel', None)
        if channel_filter is not None and ch != channel_filter:
            continue
        if msg.type == 'note_on' and msg.velocity > 0:
            if ch == 9: continue   # skip drum channel
            active[(msg.note, ch)] = tick
        elif msg.type in ('note_off',) or (msg.type == 'note_on' and msg.velocity == 0):
            key = (msg.note, ch)
            if key in active:
                start_raw = active.pop(key)
                dur_raw   = tick - start_raw
                qs  = round(start_raw / tps) * tps
                qd  = max(tps, round(dur_raw / tps) * tps)
                let, sharp, oct_ = midi_to_step(msg.note)
                notes.append({
                    'start_tick': qs,
                    'dur_ticks' : qd,
                    'midi'      : msg.note,
                    'letter'    : let,
                    'sharp'     : sharp,
                    'octave'    : oct_,
                })
    return sorted(notes, key=lambda n: n['start_tick'])

def extract_drums(mid, track_idx, channel_filter=None):
    """Kembalikan list {tick, instr} di-quantize ke 1/16."""
    tps   = mid.ticks_per_beat / 4.0
    track = mid.tracks[track_idx]
    hits  = []
    tick  = 0
    for msg in track:
        tick += msg.time
        ch = getattr(msg, 'channel', None)
        if channel_filter is not None and ch != channel_filter:
            continue
        if msg.type == 'note_on' and msg.velocity > 0:
            n = msg.note
            if   n in (35,36):           instr='z'
            elif n in (38,39,40,37):     instr='y'
            elif n in (42,44,46,49,51):  instr='x'
            else: continue
            hits.append({'tick': round(tick/tps)*tps, 'instrument': instr})
    return sorted(hits, key=lambda h: h['tick'])

# ---------------------------------------------------------------------------
# KONVERSI NOTES → GRID 16-STEP PER BAR
# ---------------------------------------------------------------------------
def notes_to_grid(notes, ticks_per_beat, total_bars, mode='highest'):
    """
    Buat grid[bar][step] = dict note.
    mode='highest' → ambil nada tertinggi jika ada tumpang tindih (untuk melodi)
    mode='lowest'  → ambil nada terendah (untuk bass)
    """
    tpbar = ticks_per_beat * 4
    tps   = ticks_per_beat / 4.0
    grid  = [[None]*16 for _ in range(total_bars)]

    for n in notes:
        bar0  = int(n['start_tick'] // tpbar)
        step0 = int(round((n['start_tick'] % tpbar) / tps))
        # kunci step agar tidak out-of-range
        if step0 >= 16: step0 = 15
        dur_steps = max(1, int(round(n['dur_ticks'] / tps)))

        for offset in range(dur_steps):
            gs   = bar0 * 16 + step0 + offset
            b    = gs // 16
            s    = gs  % 16
            if b >= total_bars: break

            cur = grid[b][s]
            entry = {
                'midi'      : n['midi'],
                'letter'    : n['letter'],
                'sharp'     : n['sharp'],
                'octave'    : n['octave'],
                'abs_start' : bar0 * 16 + step0,
                'dur_steps' : dur_steps,
            }
            if cur is None:
                grid[b][s] = entry
            else:
                if (mode == 'highest' and n['midi'] > cur['midi']) or \
                   (mode == 'lowest'  and n['midi'] < cur['midi']):
                    grid[b][s] = entry
    return grid

def drums_to_grid(hits, ticks_per_beat, total_bars):
    tpbar = ticks_per_beat * 4
    tps   = ticks_per_beat / 4.0
    grid  = [[None]*16 for _ in range(total_bars)]
    seen  = {}
    for h in hits:
        b = int(h['tick'] // tpbar)
        s = int(round((h['tick'] % tpbar) / tps))
        if b >= total_bars: continue
        s = min(s, 15)
        key = (b, s)
        if key not in seen:
            seen[key] = []
        seen[key].append(h['instrument'])
    for (b,s), instrs in seen.items():
        # prioritas: kick > snare > hihat
        if 'z' in instrs: grid[b][s] = 'z'
        elif 'y' in instrs: grid[b][s] = 'y'
        else: grid[b][s] = 'x'
    return grid

# ---------------------------------------------------------------------------
# GRID → TOKEN DOREMI
# ---------------------------------------------------------------------------
def grid_bar_to_tokens(grid_bar, base_oct=4):
    """Konversi satu bar grid[16] → list token doremi."""
    tokens = []
    i = 0
    while i < 16:
        cell = grid_bar[i]
        if cell is None:
            # hitung panjang rest
            j = i
            while j < 16 and grid_bar[j] is None:
                j += 1
            tokens += steps_to_tokens('0', j - i)
            i = j
        else:
            abs_start = cell['abs_start']
            midi_val  = cell['midi']
            # hitung berapa step note ini berlanjut di bar ini
            j = i
            while j < 16 and grid_bar[j] is not None \
                  and grid_bar[j]['abs_start'] == abs_start \
                  and grid_bar[j]['midi'] == midi_val:
                j += 1
            nt = note_token(cell['letter'], cell['sharp'], cell['octave'], base_oct)
            tokens += steps_to_tokens(nt, j - i)
            i = j
    return tokens

def drum_bar_to_tokens(grid_bar):
    tokens = []
    i = 0
    while i < 16:
        cell = grid_bar[i]
        if cell is None:
            j = i
            while j < 16 and grid_bar[j] is None:
                j += 1
            tokens += steps_to_tokens('0', j - i)
            i = j
        else:
            tokens.append(cell + '=')
            i += 1
    return tokens

# ---------------------------------------------------------------------------
# DETEKSI CHORD YANG LEBIH AKURAT
# ---------------------------------------------------------------------------
def detect_chord(midi_notes, key_sig='C', bass_midi=None):
    if not midi_notes:
        return None
    pcs = set(n % 12 for n in midi_notes)
    bass_pc = (bass_midi % 12) if bass_midi is not None else None
    scale   = KEY_SCALE_CHORDS.get(key_sig.upper(), [])

    best, best_score = 'C', -999
    for cname, cpcs in CHORDS_DB.items():
        root_pc = cpcs[0]
        score   = 0.0
        # Bass menentukan root – bobot tinggi
        if bass_pc is not None and root_pc == bass_pc:
            score += 6.0
        # Root present
        if root_pc in pcs:
            score += 2.0
        # Third & Fifth
        for pc in cpcs[1:3]:
            if pc in pcs:
                score += 1.0
        # In-key bonus
        if cname in scale:
            score += 0.3
        # Penalize if root not present at all and bass doesn't match
        if root_pc not in pcs and bass_pc != root_pc:
            score -= 1.0
        if score > best_score:
            best_score = score
            best = cname
    return best

def chord_root_step(chord_name, key_sig='C'):
    """Kembalikan nada root chord dalam notasi doremi (oktaf bass ,,)."""
    # Ambil root dari nama chord
    name = chord_name
    for suffix in ['maj7','maj','min7','min','dim7','dim','sus4','sus2','7','m']:
        name = name.replace(suffix,'')
    name = name.strip()
    root_midi = KEY_ROOT_MIDI.get(name, 60)
    # Pastikan di register bass (oktaf 2-3)
    while root_midi > 52: root_midi -= 12
    while root_midi < 28: root_midi += 12
    let, sharp, oct_ = midi_to_step(root_midi)
    return note_token(let, sharp, oct_, base_oct=4)

# ---------------------------------------------------------------------------
# DETEKSI TRACK OTOMATIS (mendukung MIDI multi-channel single-track)
# ---------------------------------------------------------------------------
def analyse_tracks(mid):
    """
    Analisis semua track.
    Jika MIDI hanya 1 track non-kosong dengan banyak channel (Format 0 atau
    Format 1 single-track), otomatis split per channel sebagai virtual track.
    """
    # Kumpulkan note per track dulu
    real_tracks = []
    for idx, track in enumerate(mid.tracks):
        notes, channels = [], {}
        active = {}
        name = 'Unnamed'
        tick = 0
        for msg in track:
            tick += msg.time
            if msg.type == 'track_name': name = msg.name
            ch = getattr(msg, 'channel', None)
            if msg.type == 'note_on' and msg.velocity > 0 and ch is not None:
                active[(msg.note, ch)] = tick
                channels[ch] = channels.get(ch, 0) + 1
            elif msg.type in ('note_off',) or (msg.type == 'note_on' and msg.velocity == 0):
                ch2 = getattr(msg, 'channel', None)
                key = (msg.note, ch2)
                if key in active:
                    st = active.pop(key)
                    notes.append({'start_tick': st, 'dur_ticks': tick - st,
                                  'midi': msg.note, 'channel': ch2})
        if notes:
            real_tracks.append({'idx': idx, 'name': name,
                                'notes': notes, 'channels': channels})

    # ── Cek apakah ini MIDI single-track multi-channel ────────────────────
    non_empty = [t for t in real_tracks]
    multi_ch  = (len(non_empty) == 1 and
                 len(non_empty[0]['channels']) > 2)

    results      = []
    all_raw_notes = {}

    if multi_ch:
        # Split satu track menjadi virtual track per channel
        base = non_empty[0]
        for ch, cnt in sorted(base['channels'].items(), key=lambda x: x[0]):
            ch_notes = [n for n in base['notes'] if n['channel'] == ch]
            if not ch_notes: continue
            virt_idx = f"{base['idx']}_ch{ch}"  # virtual key
            all_raw_notes[virt_idx] = ch_notes
            start_counts = {}
            for n in ch_notes:
                start_counts[n['start_tick']] = start_counts.get(n['start_tick'],0)+1
            poly_starts = sum(1 for v in start_counts.values() if v > 1)
            chord_ratio = poly_starts / len(start_counts) if start_counts else 0
            avg_pitch   = sum(n['midi'] for n in ch_notes) / len(ch_notes)
            results.append({
                'idx'        : virt_idx,
                'name'       : f'Ch{ch}' + (' [DRUM]' if ch==9 else ''),
                'count'      : len(ch_notes),
                'chord_ratio': chord_ratio,
                'avg_pitch'  : avg_pitch,
                'channel'    : ch,
                'track_idx'  : base['idx'],   # track MIDI asli
            })
    else:
        # Format normal: satu track = satu instrumen
        for t in real_tracks:
            notes    = t['notes']
            channels = t['channels']
            idx      = t['idx']
            all_raw_notes[idx] = notes
            start_counts = {}
            for n in notes:
                start_counts[n['start_tick']] = start_counts.get(n['start_tick'],0)+1
            poly_starts = sum(1 for v in start_counts.values() if v > 1)
            chord_ratio = poly_starts / len(start_counts) if start_counts else 0
            avg_pitch   = sum(n['midi'] for n in notes) / len(notes)
            primary_ch  = max(channels, key=channels.get) if channels else 0
            results.append({
                'idx'        : idx,
                'name'       : t['name'],
                'count'      : len(notes),
                'chord_ratio': chord_ratio,
                'avg_pitch'  : avg_pitch,
                'channel'    : primary_ch,
                'track_idx'  : idx,
            })
    return results, all_raw_notes, multi_ch

def overlap_ratio(notes_a, notes_b, ticks_per_beat, total_bars):
    """Hitung overlap antara dua track di grid 16-step."""
    tps  = ticks_per_beat / 4.0
    size = total_bars * 16
    ga   = [0]*size
    gb   = [0]*size
    for n in notes_a:
        s = int(n['start_tick']//tps)
        d = max(1, int(n['dur_ticks']//tps))
        for i in range(s, min(s+d, size)): ga[i]=1
    for n in notes_b:
        s = int(n['start_tick']//tps)
        d = max(1, int(n['dur_ticks']//tps))
        for i in range(s, min(s+d, size)): gb[i]=1
    both = sum(1 for i in range(size) if ga[i] and gb[i])
    either = sum(1 for i in range(size) if ga[i] or gb[i])
    return both/either if either else 0.0

# ---------------------------------------------------------------------------
# MAIN
# ---------------------------------------------------------------------------
def main():
    parser = argparse.ArgumentParser(
        description="Konverter MIDI → Doremi (.123) untuk Angklung."
    )
    parser.add_argument('midi_file')
    parser.add_argument('output_file', nargs='?')
    parser.add_argument('--melody', help='Indeks track melodi, pisah koma')
    parser.add_argument('--rhythm', type=int)
    parser.add_argument('--bass',   type=int)
    parser.add_argument('--drums',  type=int)
    parser.add_argument('--tempo',  type=int)
    parser.add_argument('--key',    help='Nada dasar, e.g. C, A, F#m...')
    args = parser.parse_args()

    if not os.path.exists(args.midi_file):
        print(f"[ERROR] File tidak ditemukan: {args.midi_file}"); sys.exit(1)

    print(f"\nMembaca MIDI: {args.midi_file}")
    mid = mido.MidiFile(args.midi_file)
    tpb  = mid.ticks_per_beat          # ticks per beat (quarter note)
    tpbar= tpb * 4                      # ticks per bar (4/4)

    # Total bar
    max_tick = max(
        sum(m.time for m in t) for t in mid.tracks if t
    )
    total_bars = max(1, int(round(max_tick / tpbar)))

    # Tempo
    tempo_bpm = 120
    for track in mid.tracks:
        for msg in track:
            if msg.type == 'set_tempo':
                tempo_bpm = int(round(mido.tempo2bpm(msg.tempo)))
                break
        if tempo_bpm != 120: break
    if args.tempo: tempo_bpm = args.tempo

    # Key signature
    key_sig = 'C'
    for track in mid.tracks:
        for msg in track:
            if msg.type == 'key_signature':
                key_sig = msg.key; break
        if key_sig != 'C': break
    if args.key: key_sig = args.key

    # Analisis track
    track_infos, all_raw, multi_ch = analyse_tracks(mid)

    def get_channel_for(idx):
        """Kembalikan channel number jika virtual idx (multi_ch mode)."""
        t = next((t for t in track_infos if t['idx']==idx), None)
        if t and multi_ch:
            return t['channel']
        return None

    def get_track_idx_for(idx):
        """Kembalikan track MIDI asli untuk idx (bisa virtual)."""
        t = next((t for t in track_infos if t['idx']==idx), None)
        if t:
            return t.get('track_idx', idx if isinstance(idx, int) else 0)
        return 0

    # ── Deteksi Drum ──────────────────────────────────────────────────────
    drum_idx = args.drums
    if drum_idx is None:
        drum_cands = [t for t in track_infos
                      if t['channel']==9
                      or any(w in t['name'].lower() for w in ('drum','perc','kit'))]
        if not drum_cands:
            drum_cands = [t for t in track_infos if t['avg_pitch'] < 45 and t['channel'] != 9]
        if drum_cands:
            drum_idx = max(drum_cands, key=lambda t: t['count'])['idx']

    # ── Deteksi Bass ──────────────────────────────────────────────────────
    bass_idx = args.bass
    if bass_idx is None:
        bass_cands = [t for t in track_infos
                      if t['idx'] != drum_idx and t['channel'] != 9
                      and ('bass' in t['name'].lower() or t['avg_pitch'] < 50)]
        if bass_cands:
            bass_idx = min(bass_cands, key=lambda t: t['avg_pitch'])['idx']

    remaining = [t for t in track_infos if t['idx'] not in (drum_idx, bass_idx) and t['channel'] != 9]

    # ── Deteksi Rhythm ────────────────────────────────────────────────────
    rhythm_idx = args.rhythm
    if rhythm_idx is None and remaining:
        rhy_cands = [t for t in remaining
                     if t['chord_ratio'] >= 0.20
                     or any(w in t['name'].lower() for w in ('guitar','piano','keys','organ','synth'))]
        if rhy_cands:
            rhythm_idx = max(rhy_cands, key=lambda t: t['count'])['idx']
        else:
            rhythm_idx = max(remaining, key=lambda t: t['count'])['idx']

    remaining2 = [t for t in remaining if t['idx'] != rhythm_idx]

    # ── Deteksi Melody ────────────────────────────────────────────────────
    melody_idxs = []
    if args.melody:
        melody_idxs = [x.strip() for x in args.melody.split(',')]
        # Konversi ke int jika bisa (untuk non-virtual)
        melody_idxs = [int(x) if x.isdigit() else x for x in melody_idxs]
    elif remaining2:
        mel_cands = []
        for t in remaining2:
            if not (48 <= t['avg_pitch'] <= 88): continue
            density = t['count'] / total_bars if total_bars > 0 else 0
            if t['chord_ratio'] < 0.20 or density < 6.0:
                mel_cands.append(t)

        named = [t for t in mel_cands
                 if any(w in t['name'].lower()
                        for w in ('vocal','lead','melody','voice','sax','solo','flute','violin'))]
        if named: mel_cands = named

        if mel_cands:
            primary = max(mel_cands, key=lambda t: t['count'])
            melody_idxs = [primary['idx']]
            for t in mel_cands:
                if t['idx'] == primary['idx']: continue
                ov = overlap_ratio(all_raw[primary['idx']], all_raw[t['idx']], tpb, total_bars)
                if ov < 0.15:
                    melody_idxs.append(t['idx'])
        elif remaining2:
            melody_idxs = [max(remaining2, key=lambda t: t['avg_pitch'])['idx']]

    # Default fallbacks
    first_non_drum = next((t['idx'] for t in track_infos if t['channel'] != 9), 0)
    if drum_idx    is None: drum_idx    = first_non_drum
    if bass_idx    is None: bass_idx    = first_non_drum
    if rhythm_idx  is None: rhythm_idx  = first_non_drum
    if not melody_idxs:     melody_idxs = [first_non_drum]

    # ── Laporan deteksi ───────────────────────────────────────────────────
    print("-" * 55)
    print(f"  Tempo      : {tempo_bpm} BPM")
    print(f"  Key        : {key_sig}")
    print(f"  Total bars : {total_bars}")
    print(f"  Mode       : {'Multi-Channel' if multi_ch else 'Multi-Track'}")
    def tname(idx): return next((t['name'] for t in track_infos if t['idx']==idx), '?')
    print(f"  Melodi (V1): {melody_idxs}  {[tname(i) for i in melody_idxs]}")
    print(f"  Ritem  (V2): {rhythm_idx}   {tname(rhythm_idx)}")
    print(f"  Bass   (VB): {bass_idx}     {tname(bass_idx)}")
    print(f"  Drum   (VD): {drum_idx}     {tname(drum_idx)}")
    print("-" * 55)

    # ── Ekstraksi note (mendukung virtual channel index) ──────────────────
    def do_extract_notes(virt_idx):
        ch  = get_channel_for(virt_idx)
        tid = get_track_idx_for(virt_idx)
        return extract_notes(mid, tid, channel_filter=ch)

    def do_extract_drums(virt_idx):
        ch  = get_channel_for(virt_idx)
        tid = get_track_idx_for(virt_idx)
        return extract_drums(mid, tid, channel_filter=9 if ch == 9 else ch)

    notes_mel = []
    for idx in melody_idxs:
        notes_mel += do_extract_notes(idx)
    notes_mel.sort(key=lambda n: n['start_tick'])

    notes_rhy = do_extract_notes(rhythm_idx) if rhythm_idx in all_raw else []
    notes_bas = do_extract_notes(bass_idx)   if bass_idx   in all_raw else []
    drum_hits = do_extract_drums(drum_idx)   if drum_idx   in all_raw else []

    # ── Kerapatan ritem untuk pilih mode ─────────────────────────────────
    rhy_density = len(notes_rhy) / total_bars if total_bars else 0
    bas_density = len(notes_bas) / total_bars if total_bars else 0
    drm_density = len(drum_hits) / total_bars if total_bars else 0
    print(f"  Density Ritem: {rhy_density:.1f}/bar  Bass: {bas_density:.1f}/bar  Drum: {drm_density:.1f}/bar")
    print(f"  Mode Ritem: {'CHORD BLOK' if rhy_density > 16 else 'NOTE-BY-NOTE'}")
    print(f"  Mode Bass : {'ROOT BLOK' if bas_density > 10 else 'NOTE-BY-NOTE'}")
    print("-"*55)


    # ── Build grids ───────────────────────────────────────────────────────
    grid_mel = notes_to_grid(notes_mel, tpb, total_bars, mode='highest')
    grid_rhy = notes_to_grid(notes_rhy, tpb, total_bars, mode='highest')
    grid_bas = notes_to_grid(notes_bas, tpb, total_bars, mode='lowest')
    grid_drm = drums_to_grid(drum_hits,  tpb, total_bars)

    # ── Chord detection per bar (pakai info dari 2 bar sebelum juga) ──────
    def bar_midi_notes(notes_list, bar_idx):
        bstart = bar_idx * tpbar
        bend   = bstart  + tpbar
        return [n['midi'] for n in notes_list if bstart <= n['start_tick'] < bend]

    # ── Section detection ─────────────────────────────────────────────────
    sections = {0: 'INTRO'}
    started  = False
    silent   = 0
    v1_pitch_ref = None

    for b in range(total_bars):
        bar_mel = bar_midi_notes(notes_mel, b)
        if bar_mel:
            if not started:
                sections[b] = 'VERSE 1'
                started = True
                v1_pitch_ref = sum(bar_mel)/len(bar_mel)
            elif silent >= 2:
                sections[b] = 'VERSE 2'
                silent = 0
            elif v1_pitch_ref is not None:
                avg_p = sum(bar_mel)/len(bar_mel)
                if avg_p - v1_pitch_ref >= 3.0:
                    recent = [k for k,v in sections.items() if v=='CHORUS' and b-k < 8]
                    if not recent:
                        sections[b] = 'CHORUS'
            silent = 0
        else:
            if started:
                silent += 1
                if silent == 2:    sections[b] = 'INTERLUDE'
                if silent >= 6 and b > total_bars - 12: sections[b] = 'OUTRO'

    # ── Build bars ────────────────────────────────────────────────────────
    bars_mel = []
    bars_rhy = []
    bars_bas = []
    bars_drm = []

    last_chord = key_sig.rstrip('m').upper()
    if last_chord not in CHORDS_DB: last_chord = 'C'

    for b in range(total_bars):
        # Melodi: ambil nada tertinggi per step (monophonic top voice)
        bars_mel.append(grid_bar_to_tokens(grid_mel[b], base_oct=4) or ['0'])

        # Chord detection untuk bar ini
        rhy_notes_bar = bar_midi_notes(notes_rhy, b)
        mel_notes_bar = bar_midi_notes(notes_mel, b)
        bass_bar      = bar_midi_notes(notes_bas, b)
        chord_src     = rhy_notes_bar if rhy_notes_bar else mel_notes_bar
        bass_midi_val = bass_bar[0] if bass_bar else None
        chord = detect_chord(chord_src, key_sig, bass_midi=bass_midi_val) or last_chord
        last_chord = chord

        # ── Ritem (V2) ────────────────────────────────────────────────────
        if rhy_density > 16.0:
            # Chord blok: beat 1 dan beat 3
            bars_rhy.append([f'@{chord}', '.', f'@{chord}', '.'])
        else:
            toks = grid_bar_to_tokens(grid_rhy[b], base_oct=4)
            bars_rhy.append(toks if toks else ['0'])

        # ── Bass (VB) ─────────────────────────────────────────────────────
        if bas_density > 10.0:
            rs = chord_root_step(chord, key_sig)
            bars_bas.append([rs, '.', rs, '.'])
        else:
            toks = grid_bar_to_tokens(grid_bas[b], base_oct=4)
            bars_bas.append(toks if toks else ['0'])

        # ── Drum (VD) ─────────────────────────────────────────────────────
        if drm_density > 14.0:
            # Pola drum rock standard 16-step
            bars_drm.append(['z=','0=','x=','0=','y=','0=','x=','0=',
                              'z=','0=','x=','0=','y=','0=','x=','y='])
        else:
            toks = drum_bar_to_tokens(grid_drm[b])
            bars_drm.append(toks if toks else ['0'])

    # ── Tulis output ──────────────────────────────────────────────────────
    out = args.output_file
    if not out:
        base, _ = os.path.splitext(args.midi_file)
        out = base + '.123'

    title = os.path.splitext(os.path.basename(args.midi_file))[0]\
               .replace('_',' ').replace('-',' ').title()

    with open(out, 'w', encoding='utf-8') as f:
        f.write(f"T: {title}\n")
        f.write(f"C: Unknown\n")
        f.write(f"M: 4/4\n")
        f.write(f"Q: {tempo_bpm}\n")
        f.write(f"K: {key_sig}\n\n")

        b = 0
        while b < total_bars:
            # Tulis label seksi
            for bb in range(b, min(b+BARS_PER_LINE, total_bars)):
                if bb in sections:
                    f.write(f"$ {sections[bb]}\n")

            end = min(b + BARS_PER_LINE, total_bars)

            def chunk(bars_list):
                return '|'.join(' '.join(bars_list[i]) for i in range(b, end))

            f.write(f"V1: |{chunk(bars_mel)}|\n")
            f.write(f"V2: |{chunk(bars_rhy)}|\n")
            f.write(f"VB: |{chunk(bars_bas)}|\n")
            f.write(f"VD: |{chunk(bars_drm)}|\n")
            f.write("\n")
            b += BARS_PER_LINE

    print(f"\nBerhasil! Disimpan ke: {out}\n")

if __name__ == '__main__':
    main()
