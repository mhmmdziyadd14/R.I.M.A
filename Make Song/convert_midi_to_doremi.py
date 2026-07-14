import mido
import os
import sys
import argparse

STEP_NUM = {'C':'1','D':'2','E':'3','F':'4','G':'5','A':'6','B':'7'}
BARS_PER_LINE = 4


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
        if steps == 1:
            return ['0=']
        elif steps == 2:
            return ['0-']
        elif steps == 3:
            return ['0-', '0=']
        elif steps == 4:
            return ['0']
        elif steps == 5:
            return ['0', '0=']
        elif steps == 6:
            return ['0', '0-']
        elif steps == 7:
            return ['0', '0-', '0=']
        else:
            num_rests = steps // 4
            rem_steps = steps % 4
            tokens = ['0'] * num_rests
            if rem_steps > 0:
                tokens.extend(steps_to_tokens('0', rem_steps))
            return tokens
            
    if steps == 1:
        return [note_str + '=']
    elif steps == 2:
        return [note_str + '-']
    elif steps == 3:
        return [note_str + '-', '.=']
    elif steps == 4:
        return [note_str]
    elif steps == 5:
        return [note_str, '.=']
    elif steps == 6:
        return [note_str, '.-']
    elif steps == 7:
        return [note_str, '.-', '.=']
    else:
        num_sustains = (steps - 4) // 4
        rem_steps = (steps - 4) % 4
        tokens = [note_str] + ['.'] * num_sustains
        if rem_steps > 0:
            tokens.extend(steps_to_tokens('.', rem_steps))
        return tokens

def extract_notes_from_track(mid, track_idx):
    track = mid.tracks[track_idx]
    notes = []
    current_tick = 0
    active_notes = {}
    
    for msg in track:
        current_tick += msg.time
        if msg.type == 'note_on' and msg.velocity > 0:
            active_notes[msg.note] = (current_tick, msg.velocity)
        elif (msg.type == 'note_off') or (msg.type == 'note_on' and msg.velocity == 0):
            if msg.note in active_notes:
                start_tick, vel = active_notes.pop(msg.note)
                duration = current_tick - start_tick
                
                # Apply 16th-note quantization (48 ticks)
                q_start_tick = round(start_tick / 48.0) * 48.0
                q_duration = max(48.0, round(duration / 48.0) * 48.0)
                
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
    
    for msg in track:
        current_tick += msg.time
        if msg.type == 'note_on' and msg.velocity > 0:
            note = msg.note
            instr = None
            if note in (35, 36):
                instr = 'z' # Bass Drum
            elif note in (37, 38, 39, 40):
                instr = 'y' # Snare
            elif note in (42, 44, 46, 49, 51):
                instr = 'x' # Hi-hat / Cymbal
                
            if instr:
                q_tick = round(current_tick / 48.0) * 48.0
                drum_hits.append({
                    'tick': q_tick,
                    'instrument': instr
                })
    return sorted(drum_hits, key=lambda x: x['tick'])

def track_to_grid(notes, ticks_per_beat, grid_size_per_bar, total_bars, mode='highest'):
    ticks_per_bar = ticks_per_beat * 4
    ticks_per_step = ticks_per_beat / 4.0
    
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
    ticks_per_step = ticks_per_beat / 4.0
    
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
            tokens.append(item + '=')
            i += 1
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
    
    # Calculate total bars
    max_ticks = 0
    for track in mid.tracks:
        t_ticks = 0
        for msg in track:
            t_ticks += msg.time
        max_ticks = max(max_ticks, t_ticks)
        
    total_bars = int(round(max_ticks / ticks_per_bar))
    total_bars = max(1, total_bars)
    
    # 1. Auto-detect Tempo
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
        
    # 2. Auto-detect Key Signature
    key_sig = "C"
    for track in mid.tracks:
        for msg in track:
            if msg.type == 'key_signature':
                key_sig = msg.key
                break
        if key_sig != "C":
            break
            
    # 3. Analyze Tracks
    track_infos = []
    for idx, track in enumerate(mid.tracks):
        notes = []
        channels = {}
        track_name = "Unnamed"
        for msg in track:
            if msg.type == 'track_name':
                track_name = msg.name
            if msg.type == 'note_on' and msg.velocity > 0:
                notes.append(msg.note)
                channels[msg.channel] = channels.get(msg.channel, 0) + 1
        if notes:
            primary_channel = max(channels, key=channels.get)
            avg_pitch = sum(notes) / len(notes)
            track_infos.append({
                'idx': idx,
                'name': track_name,
                'notes_count': len(notes),
                'primary_channel': primary_channel,
                'avg_pitch': avg_pitch
            })
            
    # 4. Classify Tracks
    drum_track = args.drums
    if drum_track is None:
        drum_candidates = [t for t in track_infos if t['primary_channel'] == 9]
        if drum_candidates:
            drum_track = max(drum_candidates, key=lambda x: x['notes_count'])['idx']
            
    bass_track = args.bass
    if bass_track is None:
        bass_candidates = [t for t in track_infos if t['primary_channel'] == 1]
        if not bass_candidates:
            bass_candidates = [t for t in track_infos if t['avg_pitch'] < 50 and t['primary_channel'] != 9]
        if bass_candidates:
            bass_track = min(bass_candidates, key=lambda x: x['avg_pitch'])['idx']
            
    rhythm_track = args.rhythm
    melody_tracks = []
    
    if args.melody:
        melody_tracks = [int(x.strip()) for x in args.melody.split(",")]
    
    remaining = [t for t in track_infos if t['idx'] not in (drum_track, bass_track)]
    
    if remaining:
        if rhythm_track is None:
            rhythm_candidate = max(remaining, key=lambda x: x['notes_count'])
            rhythm_track = rhythm_candidate['idx']
            
        if not melody_tracks:
            melody_candidates = [t for t in remaining if t['idx'] != rhythm_track and 50 <= t['avg_pitch'] <= 82]
            if melody_candidates:
                melody_tracks = [t['idx'] for t in melody_candidates]
            else:
                highest_track = max(remaining, key=lambda x: x['avg_pitch'])
                melody_tracks = [highest_track['idx']]
                
    # Fallbacks
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
    
    # 5. Extract Notes
    notes_melody = []
    for t_idx in melody_tracks:
        notes_melody.extend(extract_notes_from_track(mid, t_idx))
    notes_melody = sorted(notes_melody, key=lambda x: x['start_tick'])
    
    notes_rhythm = extract_notes_from_track(mid, rhythm_track)
    notes_bass   = extract_notes_from_track(mid, bass_track)
    drums        = extract_drums_from_track(mid, drum_track)
    
    # Map to Grid
    grid_size = 16
    grid_melody = track_to_grid(notes_melody, ticks_per_beat, grid_size, total_bars, mode='highest')
    grid_rhythm = track_to_grid(notes_rhythm, ticks_per_beat, grid_size, total_bars, mode='highest')
    grid_bass   = track_to_grid(notes_bass, ticks_per_beat, grid_size, total_bars, mode='lowest')
    grid_drums  = drums_to_grid(drums, ticks_per_beat, grid_size, total_bars)
    
    # Tokenize
    bars_melody = []
    bars_rhythm = []
    bars_bass   = []
    bars_drums  = []
    
    for bar_idx in range(total_bars):
        tokens_mel = grid_to_doremi_tokens(grid_melody[bar_idx], base_oct=4)
        bars_melody.append(tokens_mel if tokens_mel else ['0'])
        
        tokens_rhy = grid_to_doremi_tokens(grid_rhythm[bar_idx], base_oct=4)
        bars_rhythm.append(tokens_rhy if tokens_rhy else ['0'])
        
        tokens_bas = grid_to_doremi_tokens(grid_bass[bar_idx], base_oct=4)
        bars_bass.append(tokens_bas if tokens_bas else ['0'])
        
        tokens_drm = drum_grid_to_tokens(grid_drums[bar_idx])
        bars_drums.append(tokens_drm if tokens_drm else ['0'])
        
    # Determine Output File Path
    out_file = args.output_file
    if not out_file:
        base, _ = os.path.splitext(args.midi_file)
        out_file = base + ".123"
        
    with open(out_file, 'w', encoding='utf-8') as f:
        # Get base name for Title
        title = os.path.splitext(os.path.basename(args.midi_file))[0].replace("_", " ").title()
        f.write(f"T: {title}\n")
        f.write(f"C: Unknown\n")
        f.write(f"M: 4/4\n")
        f.write(f"Q: {tempo_bpm}\n")
        f.write(f"K: {key_sig}\n")
        f.write("\n")
        
        bar_idx = 0
        while bar_idx < total_bars:
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
