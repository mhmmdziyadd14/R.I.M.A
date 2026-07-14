import mido
import os
import sys
import argparse

STEP_NUM = {'C':'1','D':'2','E':'3','F':'4','G':'5','A':'6','B':'7'}
BARS_PER_LINE = 4

CHORDS_DB = {
    "C": [0, 4, 7], "C#": [1, 5, 8], "Db": [1, 5, 8], "D": [2, 6, 9],
    "D#": [3, 7, 10], "Eb": [3, 7, 10], "E": [4, 8, 11], "F": [5, 9, 0],
    "F#": [6, 10, 1], "Gb": [6, 10, 1], "G": [7, 11, 2], "G#": [8, 0, 3],
    "Ab": [8, 0, 3], "A": [9, 1, 4], "A#": [10, 2, 5], "Bb": [10, 2, 5], "B": [11, 3, 6],
    # Minors
    "Cm": [0, 3, 7], "C#m": [1, 4, 8], "Dm": [2, 5, 9], "D#m": [3, 6, 10],
    "Em": [4, 7, 11], "Fm": [5, 8, 0], "F#m": [6, 9, 1], "Gm": [7, 10, 2],
    "G#m": [8, 11, 3], "Am": [9, 0, 4], "Bbm": [10, 1, 5], "Bm": [11, 2, 6],
    # Diminished
    "G#dim": [8, 11, 2], "Bdim": [11, 2, 5]
}

KEY_ROOTS = {
    "C": 60, "C#": 61, "DB": 61, "D": 62, "D#": 63, "EB": 63,
    "E": 64, "F": 65, "F#": 66, "GB": 66, "G": 67, "G#": 68,
    "AB": 68, "A": 69, "A#": 70, "BB": 70, "B": 71
}

def octave_suffix(midi_octave: int, base: int = 4) -> str:
    diff = midi_octave - base
    if diff > 0:
        return "'" * diff
    elif diff < 0:
        return "," * abs(diff)
    return ""

def steps_to_tokens(note_str, steps):
    if steps <= 0:
        return []
    if note_str == '0':
        # Rest formatting for simplified 8-step (eighth-note) grid
        if steps == 1:
            return ['0-']
        elif steps == 2:
            return ['0']
        else:
            num_rests = steps // 2
            rem = steps % 2
            tokens = ['0'] * num_rests
            if rem > 0:
                tokens.append('0-')
            return tokens
            
    # Note formatting for simplified 8-step (eighth-note) grid
    if steps == 1:
        return [note_str + '-']
    elif steps == 2:
        return [note_str]
    elif steps == 3:
        return [note_str, '.-']
    elif steps == 4:
        return [note_str, '.']
    else:
        num_sustains = (steps - 2) // 2
        rem = (steps - 2) % 2
        tokens = [note_str] + ['.'] * num_sustains
        if rem > 0:
            tokens.append('.-')
        return tokens

def extract_notes_from_track(mid, track_idx):
    track = mid.tracks[track_idx]
    notes = []
    current_tick = 0
    active_notes = {}
    
    # Quantize to eighth notes (ticks_per_beat / 2)
    ticks_per_step = mid.ticks_per_beat / 2.0
    
    for msg in track:
        current_tick += msg.time
        if msg.type == 'note_on' and msg.velocity > 0:
            active_notes[msg.note] = (current_tick, msg.velocity)
        elif (msg.type == 'note_off') or (msg.type == 'note_on' and msg.velocity == 0):
            if msg.note in active_notes:
                start_tick, vel = active_notes.pop(msg.note)
                duration = current_tick - start_tick
                
                # Apply eighth-note quantization
                q_start_tick = round(start_tick / ticks_per_step) * ticks_per_step
                q_duration = max(ticks_per_step, round(duration / ticks_per_step) * ticks_per_step)
                
                names = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"]
                pitch_octave = (msg.note // 12) - 1
                pitch_name = names[msg.note % 12]
                
                acc = ''
                if '#' in pitch_name:
                    acc = '#'
                    pitch_name = pitch_name.replace('#', '')
                    
                notes.append({
                    'start_tick': q_start_tick,
                    'dur_ticks': q_duration,
                    'midi': msg.note,
                    'step': pitch_name,
                    'octave': pitch_octave,
                    'alter': 1.0 if acc == '#' else 0.0,
                    'is_rest': False
                })
    return sorted(notes, key=lambda x: x['start_tick'])

def extract_drums_from_track(mid, track_idx):
    track = mid.tracks[track_idx]
    drum_hits = []
    current_tick = 0
    ticks_per_step = mid.ticks_per_beat / 2.0
    
    for msg in track:
        current_tick += msg.time
        if msg.type == 'note_on' and msg.velocity > 0:
            note = msg.note
            instr = None
            if note in (35, 36):
                instr = 'z'
            elif note in (37, 38, 39, 40):
                instr = 'y'
            elif note in (42, 44, 46, 49, 51):
                instr = 'x'
                
            if instr:
                q_tick = round(current_tick / ticks_per_step) * ticks_per_step
                drum_hits.append({
                    'tick': q_tick,
                    'instrument': instr
                })
    return sorted(drum_hits, key=lambda x: x['tick'])

def track_to_grid(notes, ticks_per_beat, grid_size_per_bar, total_bars, mode='highest'):
    ticks_per_bar = ticks_per_beat * 4
    ticks_per_step = ticks_per_beat / 2.0
    
    grid = [[None] * grid_size_per_bar for _ in range(total_bars)]
    
    for n in notes:
        n_start_tick = n['start_tick']
        n_dur_ticks = n['dur_ticks']
        
        start_bar = int(n_start_tick // ticks_per_bar)
        start_step = int(round((n_start_tick % ticks_per_bar) / ticks_per_step))
        
        dur_steps = int(round(n_dur_ticks / ticks_per_step))
        dur_steps = max(1, dur_steps)
        
        for offset in range(dur_steps):
            total_step = (start_bar * grid_size_per_bar) + start_step + offset
            bar_idx = total_step // grid_size_per_bar
            step_idx = total_step % grid_size_per_bar
            
            if bar_idx >= total_bars:
                break
                
            current = grid[bar_idx][step_idx]
            abs_start_step = (start_bar * grid_size_per_bar) + start_step
            
            if current is None:
                grid[bar_idx][step_idx] = {
                    'midi': n['midi'],
                    'step': n['step'],
                    'octave': n['octave'],
                    'alter': n['alter'],
                    'start_step': abs_start_step,
                    'dur_steps': dur_steps
                }
            else:
                keep = False
                if mode == 'highest' and n['midi'] > current['midi']:
                    keep = True
                elif mode == 'lowest' and n['midi'] < current['midi']:
                    keep = True
                    
                if keep:
                    grid[bar_idx][step_idx] = {
                        'midi': n['midi'],
                        'step': n['step'],
                        'octave': n['octave'],
                        'alter': n['alter'],
                        'start_step': abs_start_step,
                        'dur_steps': dur_steps
                    }
    return grid

def drums_to_grid(drum_hits, ticks_per_beat, grid_size_per_bar, total_bars):
    ticks_per_bar = ticks_per_beat * 4
    ticks_per_step = ticks_per_beat / 2.0
    
    grid = [[None] * grid_size_per_bar for _ in range(total_bars)]
    
    step_hits = {}
    for hit in drum_hits:
        bar = int(hit['tick'] // ticks_per_bar)
        step = int(round((hit['tick'] % ticks_per_bar) / ticks_per_step))
        if bar >= total_bars:
            continue
        if step >= grid_size_per_bar:
            step = grid_size_per_bar - 1
            
        key = (bar, step)
        if key not in step_hits:
            step_hits[key] = []
        step_hits[key].append(hit['instrument'])
        
    for (bar, step), instrs in step_hits.items():
        if 'y' in instrs:
            grid[bar][step] = 'y'
        elif 'z' in instrs:
            grid[bar][step] = 'z'
        elif 'x' in instrs:
            grid[bar][step] = 'x'
            
    return grid

def drum_grid_to_tokens(grid_bar):
    tokens = []
    for item in grid_bar:
        if item is None:
            tokens.append('0-')
        else:
            tokens.append(item + '-')
    return tokens

def grid_to_doremi_tokens(grid_bar, base_oct):
    tokens = []
    grid_size = len(grid_bar)
    i = 0
    
    while i < grid_size:
        item = grid_bar[i]
        if item is None:
            rest_start = i
            while i < grid_size and grid_bar[i] is None:
                i += 1
            rest_steps = i - rest_start
            tokens.extend(steps_to_tokens('0', rest_steps))
        else:
            note_start_step = item['start_step']
            midi = item['midi']
            
            steps = 0
            while i < grid_size and grid_bar[i] is not None and grid_bar[i]['start_step'] == note_start_step and grid_bar[i]['midi'] == midi:
                steps += 1
                i += 1
                
            num = STEP_NUM.get(item['step'], '0')
            oct_suf = octave_suffix(item['octave'], base_oct)
            acc = '#' if item['alter'] > 0.0 else ('b' if item['alter'] < 0.0 else '')
            note_str = f"{num}{oct_suf}{acc}"
            
            tokens.extend(steps_to_tokens(note_str, steps))
            
    return tokens

def get_track_overlap(notes_a, notes_b, total_bars, ticks_per_beat, grid_size=8):
    ticks_per_step = ticks_per_beat / 2.0
    grid_a = [0] * (total_bars * grid_size)
    grid_b = [0] * (total_bars * grid_size)
    
    for n in notes_a:
        start = int(n['start_tick'] // ticks_per_step)
        dur = max(1, int(n['dur_ticks'] // ticks_per_step))
        for idx in range(start, start + dur):
            if idx < len(grid_a):
                grid_a[idx] = 1
                
    for n in notes_b:
        start = int(n['start_tick'] // ticks_per_step)
        dur = max(1, int(n['dur_ticks'] // ticks_per_step))
        for idx in range(start, start + dur):
            if idx < len(grid_b):
                grid_b[idx] = 1
                
    overlap_steps = sum(1 for i in range(len(grid_a)) if grid_a[i] == 1 and grid_b[i] == 1)
    total_active_steps = sum(1 for i in range(len(grid_a)) if grid_a[i] == 1 or grid_b[i] == 1)
    
    if total_active_steps == 0:
        return 0.0
    return overlap_steps / total_active_steps

def detect_chord_for_bar(active_midi_notes, key_sig="C"):
    if not active_midi_notes:
        return None
    pcs = set(n % 12 for n in active_midi_notes)
    best_chord = "C"
    best_score = -1
    for chord_name, chord_pcs in CHORDS_DB.items():
        score = 0
        root_pc = chord_pcs[0]
        if root_pc in pcs:
            score += 1.5
        for pc in chord_pcs[1:]:
            if pc in pcs:
                score += 1.0
        scale_chords = {
            "C": ["C", "Dm", "Em", "F", "G", "Am", "Bdim"],
            "G": ["G", "Am", "Bm", "C", "D", "Em", "F#dim"],
            "D": ["D", "Em", "F#m", "G", "A", "Bm", "C#dim"],
            "A": ["A", "Bm", "C#m", "D", "E", "F#m", "G#dim"],
            "F": ["F", "Gm", "Am", "Bb", "C", "Dm", "Bdim"],
            "Bb": ["Bb", "Cm", "Dm", "Eb", "F", "Gm", "Adim"]
        }
        if chord_name in scale_chords.get(key_sig.upper(), []):
            score += 0.1
        if score > best_score:
            best_score = score
            best_chord = chord_name
    return best_chord

def get_chord_root_step(chord_name, key_sig="C"):
    root_note = chord_name.replace("min", "").replace("maj", "").replace("dim", "").replace("m", "").upper()
    root_midi = KEY_ROOTS.get(key_sig.upper(), 60)
    chord_root_midi = KEY_ROOTS.get(root_note, 60)
    diff = (chord_root_midi - root_midi) % 12
    diff_to_step = {0: '1', 1: '1#', 2: '2', 3: '2#', 4: '3', 5: '4', 6: '4#', 7: '5', 8: '5#', 9: '6', 10: '6#', 11: '7'}
    step = diff_to_step.get(diff, '1')
    return f"{step},,"

def main():
    parser = argparse.ArgumentParser(description="Convert MIDI file to Doremi (.123) format for Angklung.")
    parser.add_argument("midi_file", help="Path to the input MIDI file.")
    parser.add_argument("output_file", nargs="?", help="Path to the output .123 file. Defaults to same name as midi.")
    parser.add_argument("--melody", help="Comma-separated track indices for V1 (Melody).")
    parser.add_argument("--rhythm", type=int, help="Track index for V2 (Rhythm).")
    parser.add_argument("--bass", type=int, help="Track index for VB (Bass).")
    parser.add_argument("--drums", type=int, help="Track index for VD (Drums).")
    parser.add_argument("--tempo", type=int, help="Custom tempo (BPM). Overrides auto-detection.")
    
    args = parser.parse_args()
    
    if not os.path.exists(args.midi_file):
        print(f"Error: File {args.midi_file} tidak ditemukan!")
        sys.exit(1)
        
    print(f"Membaca MIDI: {args.midi_file}")
    mid = mido.MidiFile(args.midi_file)
    
    ticks_per_beat = mid.ticks_per_beat
    ticks_per_bar = ticks_per_beat * 4
    
    max_ticks = 0
    for track in mid.tracks:
        t_ticks = 0
        for msg in track:
            t_ticks += msg.time
        max_ticks = max(max_ticks, t_ticks)
        
    total_bars = int(round(max_ticks / ticks_per_bar))
    total_bars = max(1, total_bars)
    
    tempo_bpm = 120
    for track in mid.tracks:
        for msg in track:
            if msg.type == 'set_tempo':
                tempo_bpm = int(round(mido.tempo2bpm(msg.tempo)))
                break
        if tempo_bpm != 120:
            break
            
    if args.tempo:
        tempo_bpm = args.tempo
        
    key_sig = "C"
    for track in mid.tracks:
        for msg in track:
            if msg.type == 'key_signature':
                key_sig = msg.key
                break
        if key_sig != "C":
            break
            
    track_infos = []
    all_notes = {}
    for idx, track in enumerate(mid.tracks):
        notes = []
        channels = {}
        track_name = "Unnamed"
        active_notes = {}
        current_tick = 0
        
        for msg in track:
            current_tick += msg.time
            if msg.type == 'track_name':
                track_name = msg.name
            if msg.type == 'note_on' and msg.velocity > 0:
                active_notes[msg.note] = current_tick
                channels[msg.channel] = channels.get(msg.channel, 0) + 1
            elif (msg.type == 'note_off') or (msg.type == 'note_on' and msg.velocity == 0):
                if msg.note in active_notes:
                    start_tick = active_notes.pop(msg.note)
                    notes.append({
                        'start_tick': start_tick,
                        'dur_ticks': current_tick - start_tick,
                        'midi': msg.note
                    })
                    
        if notes:
            all_notes[idx] = notes
            start_ticks = [n['start_tick'] for n in notes]
            ticks_counts = {}
            for t in start_ticks:
                ticks_counts[t] = ticks_counts.get(t, 0) + 1
            chord_starts = sum(1 for count in ticks_counts.values() if count > 1)
            total_unique_starts = len(ticks_counts)
            chord_ratio = chord_starts / total_unique_starts if total_unique_starts > 0 else 0
            
            primary_channel = max(channels, key=channels.get) if channels else 0
            avg_pitch = sum(n['midi'] for n in notes) / len(notes)
            
            track_infos.append({
                'idx': idx,
                'name': track_name,
                'notes_count': len(notes),
                'primary_channel': primary_channel,
                'avg_pitch': avg_pitch,
                'chord_ratio': chord_ratio
            })
            
    drum_track = args.drums
    if drum_track is None:
        drum_candidates = [t for t in track_infos if t['primary_channel'] == 9 or 'drum' in t['name'].lower() or 'perc' in t['name'].lower()]
        if drum_candidates:
            drum_track = max(drum_candidates, key=lambda x: x['notes_count'])['idx']
            
    bass_track = args.bass
    if bass_track is None:
        bass_candidates = [t for t in track_infos if t['primary_channel'] == 1 or 'bass' in t['name'].lower()]
        if not bass_candidates:
            bass_candidates = [t for t in track_infos if t['avg_pitch'] < 50 and t['idx'] != drum_track]
        if bass_candidates:
            bass_track = min(bass_candidates, key=lambda x: x['avg_pitch'])['idx']
            
    rhythm_track = args.rhythm
    melody_tracks = []
    
    if args.melody:
        melody_tracks = [int(x.strip()) for x in args.melody.split(",")]
        
    remaining = [t for t in track_infos if t['idx'] not in (drum_track, bass_track)]
    
    if remaining:
        if rhythm_track is None:
            rhythm_candidates = [t for t in remaining if t['chord_ratio'] >= 0.20 or 'guitar' in t['name'].lower() or 'piano' in t['name'].lower()]
            if rhythm_candidates:
                rhythm_track = max(rhythm_candidates, key=lambda x: x['notes_count'])['idx']
            else:
                rhythm_candidate = max(remaining, key=lambda x: x['notes_count'])
                rhythm_track = rhythm_candidate['idx']
                
        if not melody_tracks:
            melody_candidates = [t for t in remaining if t['idx'] != rhythm_track and t['chord_ratio'] < 0.15 and 50 <= t['avg_pitch'] <= 82]
            named_vocals = [t for t in melody_candidates if any(w in t['name'].lower() for w in ['vocal', 'melod', 'lead', 'sing', 'voice', 'sax', 'solo'])]
            if named_vocals:
                melody_candidates = named_vocals
                
            if melody_candidates:
                primary_mel = max(melody_candidates, key=lambda x: x['notes_count'])
                melody_tracks = [primary_mel['idx']]
                for t in melody_candidates:
                    if t['idx'] != primary_mel['idx']:
                        overlap = get_track_overlap(all_notes[primary_mel['idx']], all_notes[t['idx']], total_bars, ticks_per_beat)
                        if overlap < 0.15:
                            melody_tracks.append(t['idx'])
            else:
                highest_track = max(remaining, key=lambda x: x['avg_pitch'])
                melody_tracks = [highest_track['idx']]
                
    if drum_track is None: drum_track = 0
    if bass_track is None: bass_track = 0
    if rhythm_track is None: rhythm_track = 0
    if not melody_tracks: melody_tracks = [0]
    
    print("-" * 50)
    print("DETEKSI OTOMATIS KONFIGURASI:")
    print(f"  Tempo (BPM)      : {tempo_bpm}")
    print(f"  Nada Dasar (Key) : {key_sig}")
    print(f"  Melodi (V1)      : Track {melody_tracks} {[t['name'] for t in track_infos if t['idx'] in melody_tracks]}")
    print(f"  Ritem (V2)       : Track {rhythm_track} {[t['name'] for t in track_infos if t['idx'] == rhythm_track]}")
    print(f"  Bass (VB)        : Track {bass_track} {[t['name'] for t in track_infos if t['idx'] == bass_track]}")
    print(f"  Drum (VD)        : Track {drum_track} {[t['name'] for t in track_infos if t['idx'] == drum_track]}")
    print("-" * 50)
    
    notes_melody = []
    for t_idx in melody_tracks:
        notes_melody.extend(extract_notes_from_track(mid, t_idx))
    notes_melody = sorted(notes_melody, key=lambda x: x['start_tick'])
    
    notes_rhythm = extract_notes_from_track(mid, rhythm_track) if rhythm_track in all_notes else []
    notes_bass   = extract_notes_from_track(mid, bass_track) if bass_track in all_notes else []
    drums        = extract_drums_from_track(mid, drum_track) if drum_track in all_notes else []
    
    # 6. Section Detection (8-step grid)
    sections = {}
    sections[0] = "INTRO"
    has_started = False
    silent_count = 0
    verse_avg_pitch = None
    first_active_pitches = []
    
    for b in range(total_bars):
        bar_notes = [n for n in notes_melody if b * ticks_per_bar <= n['start_tick'] < (b + 1) * ticks_per_bar]
        if bar_notes:
            if not has_started:
                sections[b] = "VERSE 1"
                has_started = True
                first_active_pitches = [n['midi'] for n in bar_notes]
                verse_avg_pitch = sum(first_active_pitches) / len(first_active_pitches)
            elif silent_count >= 2:
                sections[b] = "VERSE 2"
                silent_count = 0
            elif verse_avg_pitch is not None:
                bar_avg_pitch = sum(n['midi'] for n in bar_notes) / len(bar_notes)
                if bar_avg_pitch - verse_avg_pitch >= 3.0:
                    recent_ch = [k for k, v in sections.items() if v == "CHORUS" and b - k < 8]
                    if not recent_ch:
                        sections[b] = "CHORUS"
            silent_count = 0
        else:
            if has_started:
                silent_count += 1
                if silent_count == 2:
                    sections[b] = "INTERLUDE"
                elif silent_count >= 6 and b > total_bars - 10:
                    sections[b] = "OUTRO"
                    
    # 7. Simplified 8-Step Grid generation
    bars_melody = []
    bars_rhythm = []
    bars_bass   = []
    bars_drums  = []
    
    grid_size = 8
    grid_melody = track_to_grid(notes_melody, ticks_per_beat, grid_size, total_bars, mode='highest')
    grid_drums  = drums_to_grid(drums, ticks_per_beat, grid_size, total_bars)
    
    last_chord = "C"
    
    for bar_idx in range(total_bars):
        tokens_mel = grid_to_doremi_tokens(grid_melody[bar_idx], base_oct=4)
        bars_melody.append(tokens_mel if tokens_mel else ['0'])
        
        bar_start_tick = bar_idx * ticks_per_bar
        bar_end_tick = (bar_idx + 1) * ticks_per_bar
        
        active_notes = [n['midi'] for n in notes_rhythm if bar_start_tick <= n['start_tick'] < bar_end_tick]
        if not active_notes:
            active_notes = [n['midi'] for n in notes_melody if bar_start_tick <= n['start_tick'] < bar_end_tick]
            
        chord_name = detect_chord_for_bar(active_notes, key_sig)
        if chord_name:
            last_chord = chord_name
        else:
            chord_name = last_chord
            
        mel_has_notes = any(x is not None for x in grid_melody[bar_idx])
        if not mel_has_notes and bar_idx < 4:
            bars_rhythm.append(['0'])
            bars_bass.append(['0'])
        else:
            chord_token = f"@{chord_name}"
            # Beat 1 (chord) + Beat 2 (sustain) + Beat 3 (chord) + Beat 4 (sustain)
            bars_rhythm.append([chord_token, '.', chord_token, '.'])
            
            root_step = get_chord_root_step(chord_name, key_sig)
            bars_bass.append([root_step, '.', root_step, '.'])
            
        has_drums = any(x is not None for x in grid_drums[bar_idx])
        if not has_drums:
            if mel_has_notes or bar_idx >= 4:
                # Simple rock beat in 8-step: bass(z) snare(y) bass(z) snare(y) with hihat(x)
                bars_drums.append(['z-', 'x-', 'y-', 'x-', 'z-', 'x-', 'y-', 'x-'])
            else:
                bars_drums.append(['0'])
        else:
            tokens_drm = drum_grid_to_tokens(grid_drums[bar_idx])
            bars_drums.append(tokens_drm if tokens_drm else ['0'])
            
    out_file = args.output_file
    if not out_file:
        base, _ = os.path.splitext(args.midi_file)
        out_file = base + ".123"
        
    with open(out_file, 'w', encoding='utf-8') as f:
        title = os.path.splitext(os.path.basename(args.midi_file))[0].replace("_", " ").title()
        f.write(f"T: {title}\n")
        f.write(f"C: Unknown\n")
        f.write(f"M: 4/4\n")
        f.write(f"Q: {tempo_bpm}\n")
        f.write(f"K: {key_sig}\n")
        f.write("\n")
        
        bar_idx = 0
        while bar_idx < total_bars:
            for b in range(bar_idx, min(bar_idx + BARS_PER_LINE, total_bars)):
                if b in sections:
                    f.write(f"$ {sections[b]}\n")
                    
            chunk_end = min(bar_idx + BARS_PER_LINE, total_bars)
            
            chunk_mel = bars_melody[bar_idx:chunk_end]
            mel_str = "|".join(" ".join(b) for b in chunk_mel)
            f.write(f"V1: |{mel_str}|\n")
            
            chunk_rhy = bars_rhythm[bar_idx:chunk_end]
            rhy_str = "|".join(" ".join(b) for b in chunk_rhy)
            f.write(f"V2: |{rhy_str}|\n")
            
            chunk_bas = bars_bass[bar_idx:chunk_end]
            bas_str = "|".join(" ".join(b) for b in chunk_bas)
            f.write(f"VB: |{bas_str}|\n")
            
            chunk_drm = bars_drums[bar_idx:chunk_end]
            drm_str = "|".join(" ".join(b) for b in chunk_drm)
            f.write(f"VD: |{drm_str}|\n")
            
            f.write("\n")
            bar_idx += BARS_PER_LINE
            
    print(f"Berhasil mengkonversi MIDI ke Doremi! File disimpan: {out_file}")

if __name__ == '__main__':
    main()
