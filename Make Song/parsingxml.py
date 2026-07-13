"""
parsingxml.py  v6.0
Mengkonversi file MusicXML (.musicxml) ke format Doremi .123 yang sederhana.
Mengekstrak 3 peran penting: Melodi (V1), Ritem (V2), dan Bass (VB).

Format .123:
  T: Judul
  C: Composer
  M: meter (4/4, 3/4, dll)
  Q: tempo bpm
  K: kunci (C, G, Dm, dll)

  $ BAGIAN
  V1: |not not not|not not not|...  (Melodi)
  V2: |not not not|...              (Ritem)
  VB: |not not not|...              (Bass)
"""

import xml.etree.ElementTree as ET
import os
import sys
import re

# ─────────────────────────────────────────────
# KONFIGURASI
# ─────────────────────────────────────────────
INPUT_FILE  = 'DontLookBackInAnger-Oasis.musicxml'
OUTPUT_FILE = 'Oasis.123'
BARS_PER_LINE = 4   # jumlah bar per baris di output

# Map step → angka diatonis
STEP_NUM = {'C':'1','D':'2','E':'3','F':'4','G':'5','A':'6','B':'7'}

# Map fifths → nama kunci
FIFTHS_KEY = {
     0:'C',  1:'G',  2:'D',  3:'A',  4:'E',  5:'B',  6:'F#',
    -1:'F', -2:'Bb',-3:'Eb',-4:'Ab',-5:'Db',-6:'Gb'
}

# ─────────────────────────────────────────────
# HELPER: suffix oktaf
# ─────────────────────────────────────────────
def octave_suffix(midi_octave: int, base: int = 5) -> str:
    diff = midi_octave - base
    if diff > 0:
        return "'" * diff
    elif diff < 0:
        return "," * abs(diff)
    return ""

# ─────────────────────────────────────────────
# HELPER: durasi → suffix + filler tokens
# ─────────────────────────────────────────────
def steps_to_tokens(note_str, steps):
    if steps <= 0:
        return []
    if note_str == '0':
        # Rest formatting
        if steps == 1:
            return ['0=']
        elif steps == 2:
            return ['0-']
        elif steps == 3:
            return ['0-/']
        elif steps == 4:
            return ['0']
        elif steps == 6:
            return ['0/']
        elif steps == 7:
            return ['0//']
        elif steps >= 8:
            num_rests = steps // 4
            rem_steps = steps % 4
            tokens = ['0'] * num_rests
            if rem_steps > 0:
                tokens.extend(steps_to_tokens('0', rem_steps))
            return tokens
        elif steps == 5:
            return ['0', '0=']
        else:
            return ['0']
            
    # Note formatting
    if steps == 1:
        return [note_str + '=']
    elif steps == 2:
        return [note_str + '-']
    elif steps == 3:
        return [note_str + '-/']
    elif steps == 4:
        return [note_str]
    elif steps == 6:
        return [note_str + '/']
    elif steps == 7:
        return [note_str + '//']
    elif steps >= 8:
        num_sustains = (steps - 4) // 4
        rem_steps = (steps - 4) % 4
        tokens = [note_str] + ['.'] * num_sustains
        if rem_steps > 0:
            tokens.extend(steps_to_tokens('.', rem_steps))
        return tokens
    elif steps == 5:
        return [note_str, '.=']
    else:
        return [note_str]

# ─────────────────────────────────────────────
# PARSE XML (strip namespace)
# ─────────────────────────────────────────────
def parse_xml(filepath):
    tree = ET.parse(filepath)
    root = tree.getroot()
    for el in root.iter():
        if '}' in el.tag:
            el.tag = el.tag.split('}', 1)[1]
    return root

# ─────────────────────────────────────────────
# AMBIL METADATA
# ─────────────────────────────────────────────
def get_metadata(root):
    def txt(el):
        return el.text.strip() if el is not None and el.text else None

    title = (txt(root.find('.//work/work-title')) or
             txt(root.find('.//movement-title')) or
             txt(root.find('.//credit-words')) or
             None)

    composer = txt(root.find('.//identification/creator[@type="composer"]'))
    if not composer:
        composer = txt(root.find('.//identification/creator'))

    tempo = '120'
    se = root.find('.//sound[@tempo]')
    if se is not None:
        tempo = str(int(float(se.get('tempo', '120'))))
    else:
        me = root.find('.//metronome/per-minute')
        if me is not None and me.text:
            tempo = me.text.strip().split('.')[0]

    b  = root.find('.//time/beats')
    bt = root.find('.//time/beat-type')
    meter = f"{b.text.strip()}/{bt.text.strip()}" if (b is not None and bt is not None) else '4/4'

    kf = root.find('.//key/fifths')
    km = root.find('.//key/mode')
    key = 'C'
    if kf is not None:
        fifths = int(kf.text.strip())
        key = FIFTHS_KEY.get(fifths, 'C')
        if km is not None and km.text and 'minor' in km.text.lower():
            key = key + 'm'

    div_el = root.find('.//divisions')
    divisions = int(div_el.text.strip()) if div_el is not None else 1440

    return {
        'title':    title or 'Unknown',
        'composer': composer or '',
        'tempo':    tempo,
        'meter':    meter,
        'key':      key,
        'divisions':divisions,
    }

# ─────────────────────────────────────────────
# HITUNG INFO PART (notes, avg octave, dll)
# ─────────────────────────────────────────────
def part_info(part_el):
    octs = []
    for n in part_el.findall('.//note'):
        if n.find('rest') is not None:
            continue
        p = n.find('pitch')
        if p is not None:
            o = p.find('octave')
            if o is not None:
                octs.append(int(o.text.strip()))
    if not octs:
        return 0, 4.0, 4, 4
    return len(octs), sum(octs)/len(octs), min(octs), max(octs)

# ─────────────────────────────────────────────
# PARSE MEASURE NOTES WITH TIME-TRACKING
# ─────────────────────────────────────────────
def parse_measure_notes(measure_el, divisions):
    notes = []
    current_time = 0
    
    for el in measure_el:
        if el.tag == 'backup':
            dur_text = el.find('duration').text.strip() if el.find('duration') is not None else '0'
            dur = int(dur_text) if dur_text.isdigit() else 0
            current_time -= dur
        elif el.tag == 'forward':
            dur_text = el.find('duration').text.strip() if el.find('duration') is not None else '0'
            dur = int(dur_text) if dur_text.isdigit() else 0
            current_time += dur
        elif el.tag == 'note':
            dur_el = el.find('duration')
            if dur_el is not None:
                try:
                    dur = int(dur_el.text.strip())
                except:
                    dur = divisions
            else:
                dur = 0
                
            is_chord = el.find('chord') is not None
            if is_chord:
                start_time = current_time - dur
            else:
                start_time = current_time
                current_time += dur
                
            is_rest = el.find('rest') is not None
            
            if is_rest:
                notes.append({
                    'start_tick': max(0, start_time),
                    'dur_ticks': dur,
                    'midi': None,
                    'step': '0',
                    'octave': 0,
                    'alter': 0.0,
                    'is_rest': True
                })
            else:
                pitch = el.find('pitch')
                if pitch is not None:
                    step = pitch.find('step').text.strip() if pitch.find('step') is not None else 'C'
                    octave = int(pitch.find('octave').text.strip()) if pitch.find('octave') is not None else 4
                    alter = float(pitch.find('alter').text.strip()) if pitch.find('alter') is not None else 0.0
                    
                    step_to_semitone = {'C':0, 'D':2, 'E':4, 'F':5, 'G':7, 'A':9, 'B':11}
                    midi = 12 * (octave + 1) + step_to_semitone.get(step, 0) + int(alter)
                    
                    notes.append({
                        'start_tick': max(0, start_time),
                        'dur_ticks': dur,
                        'midi': midi,
                        'step': step,
                        'octave': octave,
                        'alter': alter,
                        'is_rest': False
                    })
    return notes

# ─────────────────────────────────────────────
# MAP MEASURE TO RESOLUTION GRID
# ─────────────────────────────────────────────
def measure_to_grid(notes, divisions, grid_size, mode='highest'):
    grid = [None] * grid_size
    ticks_per_step = divisions / 4.0
    
    for n in notes:
        if n['is_rest']:
            continue
            
        n_start_step = int(round(n['start_tick'] / ticks_per_step))
        n_dur_steps = int(round(n['dur_ticks'] / ticks_per_step))
        n_dur_steps = max(1, n_dur_steps)
        
        if n_start_step >= grid_size:
            continue
            
        for offset in range(n_dur_steps):
            step_idx = n_start_step + offset
            if step_idx >= grid_size:
                break
                
            current = grid[step_idx]
            if current is None:
                grid[step_idx] = {
                    'midi': n['midi'],
                    'step': n['step'],
                    'octave': n['octave'],
                    'alter': n['alter'],
                    'start_step': n_start_step,
                    'dur_steps': n_dur_steps
                }
            else:
                keep = False
                if mode == 'highest' and n['midi'] > current['midi']:
                    keep = True
                elif mode == 'lowest' and n['midi'] < current['midi']:
                    keep = True
                    
                if keep:
                    grid[step_idx] = {
                        'midi': n['midi'],
                        'step': n['step'],
                        'octave': n['octave'],
                        'alter': n['alter'],
                        'start_step': n_start_step,
                        'dur_steps': n_dur_steps
                    }
    return grid

# ─────────────────────────────────────────────
# GRID → DOREMI TOKENS
# ─────────────────────────────────────────────
def grid_to_doremi_tokens(grid, base_oct):
    tokens = []
    grid_size = len(grid)
    i = 0
    
    while i < grid_size:
        item = grid[i]
        if item is None:
            rest_start = i
            while i < grid_size and grid[i] is None:
                i += 1
            rest_steps = i - rest_start
            tokens.extend(steps_to_tokens('0', rest_steps))
        else:
            note_start_step = item['start_step']
            midi = item['midi']
            
            steps = 0
            while i < grid_size and grid[i] is not None and grid[i]['start_step'] == note_start_step and grid[i]['midi'] == midi:
                steps += 1
                i += 1
                
            num = STEP_NUM.get(item['step'], '0')
            oct_suf = octave_suffix(item['octave'], base_oct)
            acc = '#' if item['alter'] > 0.0 else ('b' if item['alter'] < 0.0 else '')
            note_str = f"{num}{oct_suf}{acc}"
            
            tokens.extend(steps_to_tokens(note_str, steps))
            
    return tokens

# ─────────────────────────────────────────────
# PROSES SATU PART → list bar (grid-aligned)
# ─────────────────────────────────────────────
def process_part(part_el, divisions, base_oct, meter_meta, mode='highest'):
    try:
        parts = meter_meta.split('/')
        num = int(parts[0])
        den = int(parts[1])
    except:
        num = 4
        den = 4
        
    measure_duration_quarters = num * (4.0 / den)
    grid_size = int(measure_duration_quarters * 4)
    
    bars = []
    for measure_el in part_el.findall('measure'):
        notes = parse_measure_notes(measure_el, divisions)
        grid = measure_to_grid(notes, divisions, grid_size, mode=mode)
        tokens = grid_to_doremi_tokens(grid, base_oct)
        bars.append(tokens if tokens else ['0'])
        
    return bars

# ─────────────────────────────────────────────
# DETEKSI SECTION
# ─────────────────────────────────────────────
def detect_sections(root):
    sections = {}
    known_sections = {
        'intro','verse','chorus','bridge','outro','coda','reff',
        'interlude','solo','pre-chorus','refrain','refren',
        'lagu', 'bagian', 'a','b','c','d',
    }

    for measure_el in root.findall('.//measure'):
        m_num = measure_el.get('number', '')
        for dir_el in measure_el.findall('direction'):
            reh = dir_el.find('.//rehearsal')
            if reh is not None and reh.text:
                sections[m_num] = reh.text.strip()
                continue
            words = dir_el.find('.//words')
            if words is not None and words.text:
                txt = words.text.strip()
                lo = txt.lower()
                if (len(txt) <= 25 and
                    (txt.isupper() or
                     any(kw in lo for kw in known_sections))):
                    if m_num not in sections:
                        sections[m_num] = txt
    return sections

# ─────────────────────────────────────────────
# SELEKSI KATEGORI PART: MELODI, RITEM, BASS
# ─────────────────────────────────────────────
def select_melody_rhythm_bass(root, all_parts):
    candidates = []
    
    score_parts = root.findall('.//part-list/score-part')
    for sp in score_parts:
        pid = sp.get('id', '')
        pname_el = sp.find('part-name')
        pname = pname_el.text.strip().lower() if pname_el is not None and pname_el.text else ''

        if any(k in pname for k in ('drum', 'perc', 'cymbal', 'snare', 'bass drum')):
            continue

        part_el = root.find(f'.//part[@id="{pid}"]')
        if part_el is None:
            continue

        cnt, avg_oct, mn, mx = part_info(part_el)
        if cnt < 10:
            continue

        candidates.append({
            'part_el': part_el,
            'pid': pid,
            'name': pname,
            'count': cnt,
            'avg_oct': avg_oct
        })

    if not candidates:
        return []

    max_notes = max(c['count'] for c in candidates)
    min_notes_threshold = max_notes * 0.10

    main_candidates = [c for c in candidates if c['count'] >= min_notes_threshold]

    if not main_candidates:
        main_candidates = candidates

    main_candidates.sort(key=lambda x: x['avg_oct'])
    bass_cand = main_candidates[0]

    remaining = [c for c in main_candidates if c != bass_cand]
    melody_cand = None
    if remaining:
        remaining.sort(key=lambda x: -x['avg_oct'])
        melody_cand = remaining[0]

    remaining = [c for c in main_candidates if c != bass_cand and c != melody_cand]
    rhythm_cand = None
    if remaining:
        remaining.sort(key=lambda x: -x['count'])
        rhythm_cand = remaining[0]

    selected = []
    if melody_cand:
        selected.append((melody_cand['part_el'], melody_cand['count'], melody_cand['avg_oct'], 'V1', 'Melodi'))
    if rhythm_cand:
        selected.append((rhythm_cand['part_el'], rhythm_cand['count'], rhythm_cand['avg_oct'], 'V2', 'Ritem'))
    if bass_cand:
        selected.append((bass_cand['part_el'], bass_cand['count'], bass_cand['avg_oct'], 'VB', 'Bass'))

    order = {'V1': 0, 'V2': 1, 'VB': 2}
    selected.sort(key=lambda x: order.get(x[3], 9))
    
    return selected

# ─────────────────────────────────────────────
# TULIS FILE .123
# ─────────────────────────────────────────────
def write_123(output_path, meta, voice_data, sections, total_bars, filename_title=None):
    title = meta['title']
    if title == 'Unknown' and filename_title:
        title = filename_title

    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(f"T: {title}\n")
        if meta['composer']:
            f.write(f"C: {meta['composer']}\n")
        f.write(f"M: {meta['meter']}\n")
        f.write(f"Q: {meta['tempo']}\n")
        f.write(f"K: {meta['key']}\n")
        f.write("\n")

        current_section = None
        bar_idx = 0

        while bar_idx < total_bars:
            chunk_end = min(bar_idx + BARS_PER_LINE, total_bars)

            for bi in range(bar_idx, chunk_end):
                m_str = str(bi + 1)
                if m_str in sections and sections[m_str] != current_section:
                    current_section = sections[m_str]
                    f.write(f"\n$ {current_section}\n")
                    break

            for label, name, bars in voice_data:
                chunk = bars[bar_idx:chunk_end]
                bar_strs = [' '.join(b) for b in chunk]
                line = f"{label}: |{'|'.join(bar_strs)}|\n"
                f.write(line)

            f.write("\n")
            bar_idx += BARS_PER_LINE

# ─────────────────────────────────────────────
# MAIN CONVERT
# ─────────────────────────────────────────────
def convert(input_path, output_path):
    print(f"Membaca: {input_path}")
    root = parse_xml(input_path)

    meta      = get_metadata(root)
    divisions = meta['divisions']
    sections  = detect_sections(root)

    filename_title = os.path.splitext(os.path.basename(input_path))[0].replace('_', ' ').replace('-', ' ')

    print(f"  Judul    : {meta['title']} (file: {filename_title})")
    print(f"  Composer : {meta['composer'] or '-'}")
    print(f"  Tempo    : {meta['tempo']} bpm")
    print(f"  Meter    : {meta['meter']}")
    print(f"  Kunci    : {meta['key']}")
    print(f"  Divisions: {divisions}")

    all_parts = root.findall('.//part')
    print(f"  Total part: {len(all_parts)}")

    selected = select_melody_rhythm_bass(root, all_parts)

    voice_data = []
    total_bars = 0

    for part_el, cnt, avg_oct, label, name in selected:
        if label == 'V1':
            base_oct = 5
            mode = 'highest'
        elif label == 'VB':
            base_oct = 3
            mode = 'lowest'
        else:
            base_oct = 4
            mode = 'highest'

        print(f"  {label} ({name}): {cnt} not, avg oktaf={avg_oct:.1f}, base_oct={base_oct}")
        bars = process_part(part_el, divisions, base_oct, meta['meter'], mode=mode)
        total_bars = max(total_bars, len(bars))
        voice_data.append((label, name, bars))

    print(f"  Total bar : {total_bars}")
    if sections:
        print(f"  Sections  : {list(sections.values())[:8]}")

    write_123(output_path, meta, voice_data, sections, total_bars, filename_title)
    print(f"\nBerhasil! File disimpan: {output_path}")

# ─────────────────────────────────────────────
# ENTRY POINT
# ─────────────────────────────────────────────
if __name__ == '__main__':
    if len(sys.argv) >= 3:
        inp = sys.argv[1]
        out = sys.argv[2]
    elif len(sys.argv) == 2:
        inp = sys.argv[1]
        base = os.path.splitext(os.path.basename(inp))[0]
        out  = base + '.123'
    else:
        inp = INPUT_FILE
        out = OUTPUT_FILE

    if not os.path.exists(inp):
        print(f"ERROR: File '{inp}' tidak ditemukan!")
        sys.exit(1)

    convert(inp, out)