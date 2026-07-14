import mido
import os
import sys

# ─────────────────────────────────────────────
# CONFIGURATION
# ─────────────────────────────────────────────
MIDI_FILE   = 'oasis-dont_look_back_in_anger.mid'
OUTPUT_FILE = 'Oasis.123'

# Track mapping (0-indexed indices from inspect_midi.py)
TRACK_MELODY = 4   # V1 (Melody - Vocal)
TRACK_RHYTHM = 2   # V2 (Rhythm - Piano)
TRACK_BASS   = 3   # VB (Bass)
TRACK_DRUMS  = 11  # VD (Drums)

BARS_PER_LINE = 4

STEP_NUM = {'C':'1','D':'2','E':'3','F':'4','G':'5','A':'6','B':'7'}

# ─────────────────────────────────────────────
# HELPER FUNCTIONS
# ─────────────────────────────────────────────
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
        if steps == 1: return ['0=']
        elif steps == 2: return ['0-']
        elif steps == 3: return ['0-/']
        elif steps == 4: return ['0']
        elif steps == 6: return ['0/']
        elif steps == 7: return ['0//']
        elif steps >= 8:
            num_rests = steps // 4
            rem_steps = steps % 4
            tokens = ['0'] * num_rests
            if rem_steps > 0:
                tokens.extend(steps_to_tokens('0', rem_steps))
            return tokens
        elif steps == 5: return ['0', '0=']
        else: return ['0']
        
    if steps == 1: return [note_str + '=']
    elif steps == 2: return [note_str + '-']
    elif steps == 3: return [note_str + '-/']
    elif steps == 4: return [note_str]
    elif steps == 6: return [note_str + '/']
    elif steps == 7: return [note_str + '//']
    elif steps >= 8:
        num_sustains = (steps - 4) // 4
        rem_steps = (steps - 4) % 4
        tokens = [note_str] + ['.'] * num_sustains
        if rem_steps > 0:
            tokens.extend(steps_to_tokens('.', rem_steps))
        return tokens
    elif steps == 5: return [note_str, '.=']
    else: return [note_str]

# ─────────────────────────────────────────────
# EXTRACT NOTES FROM MIDI TRACK
# ─────────────────────────────────────────────
def extract_notes_from_track(mid, track_idx):
    track = mid.tracks[track_idx]
    notes = []
    current_tick = 0
    active_notes = {} # midi -> (start_tick, velocity)
    
    for msg in track:
        current_tick += msg.time
        if msg.type == 'note_on' and msg.velocity > 0:
            active_notes[msg.note] = (current_tick, msg.velocity)
        elif (msg.type == 'note_off') or (msg.type == 'note_on' and msg.velocity == 0):
            if msg.note in active_notes:
                start_tick, vel = active_notes.pop(msg.note)
                duration = current_tick - start_tick
                
                names = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"]
                pitch_octave = (msg.note // 12) - 1
                pitch_name = names[msg.note % 12]
                
                acc = ''
                if '#' in pitch_name:
                    acc = '#'
                    pitch_name = pitch_name.replace('#', '')
                    
                notes.append({
                    'start_tick': start_tick,
                    'dur_ticks': duration,
                    'midi': msg.note,
                    'step': pitch_name,
                    'octave': pitch_octave,
                    'alter': 1.0 if acc == '#' else 0.0,
                    'is_rest': False
                })
    return sorted(notes, key=lambda x: x['start_tick'])

# ─────────────────────────────────────────────
# EXTRACT DRUMS FROM MIDI TRACK
# ─────────────────────────────────────────────
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
                instr = 'y' # Snare / Clap / Stick
            elif note in (42, 44, 46, 49, 51):
                instr = 'x' # Hi-hat / Cymbals / Ride
                
            if instr:
                drum_hits.append({
                    'tick': current_tick,
                    'instrument': instr
                })
    return sorted(drum_hits, key=lambda x: x['tick'])

# ─────────────────────────────────────────────
# MAP TRACK TO GRID
# ─────────────────────────────────────────────
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

# ─────────────────────────────────────────────
# MAP DRUMS TO GRID
# ─────────────────────────────────────────────
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

# ─────────────────────────────────────────────
# CONVERT DRUM GRID TO TOKENS
# ─────────────────────────────────────────────
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

# ─────────────────────────────────────────────
# MAIN CONVERT
# ─────────────────────────────────────────────
def convert():
    print(f"Membaca MIDI: {MIDI_FILE}")
    mid = mido.MidiFile(MIDI_FILE)
    
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
    
    print(f"  Ticks Per Beat: {ticks_per_beat}")
    print(f"  Total Bar     : {total_bars}")
    
    notes_melody = extract_notes_from_track(mid, TRACK_MELODY)
    notes_rhythm = extract_notes_from_track(mid, TRACK_RHYTHM)
    notes_bass   = extract_notes_from_track(mid, TRACK_BASS)
    drums        = extract_drums_from_track(mid, TRACK_DRUMS)
    
    grid_size = 16
    grid_melody = track_to_grid(notes_melody, ticks_per_beat, grid_size, total_bars, mode='highest')
    grid_rhythm = track_to_grid(notes_rhythm, ticks_per_beat, grid_size, total_bars, mode='highest')
    grid_bass   = track_to_grid(notes_bass, ticks_per_beat, grid_size, total_bars, mode='lowest')
    grid_drums  = drums_to_grid(drums, ticks_per_beat, grid_size, total_bars)
    
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
        
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write(f"T: Dont Look Back in Anger\n")
        f.write(f"C: Oasis\n")
        f.write(f"M: 4/4\n")
        f.write(f"Q: 120\n")
        f.write(f"K: C\n")
        f.write("\n")
        
        bar_idx = 0
        while bar_idx < total_bars:
            chunk_end = min(bar_idx + BARS_PER_LINE, total_bars)
            
            # V1
            chunk_mel = bars_melody[bar_idx:chunk_end]
            mel_str = "|".join(" ".join(b) for b in chunk_mel)
            f.write(f"V1: |{mel_str}|\n")
            
            # V2
            chunk_rhy = bars_rhythm[bar_idx:chunk_end]
            rhy_str = "|".join(" ".join(b) for b in chunk_rhy)
            f.write(f"V2: |{rhy_str}|\n")
            
            # VB
            chunk_bas = bars_bass[bar_idx:chunk_end]
            bas_str = "|".join(" ".join(b) for b in chunk_bas)
            f.write(f"VB: |{bas_str}|\n")
            
            # VD
            chunk_drm = bars_drums[bar_idx:chunk_end]
            drm_str = "|".join(" ".join(b) for b in chunk_drm)
            f.write(f"VD: |{drm_str}|\n")
            
            f.write("\n")
            bar_idx += BARS_PER_LINE
            
    print(f"Berhasil mengkonversi MIDI ke Doremi! File disimpan: {OUTPUT_FILE}")

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

if __name__ == '__main__':
    convert()
