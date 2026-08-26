// ====================================================================
// R.I.M.A SPA WEB CONTROLLER APPLICATION LOGIC
// ====================================================================

let settings = {
  port1: localStorage.getItem('rima_port_1') || 'COM10',
  port2: localStorage.getItem('rima_port_2') || 'COM11',
  port3: localStorage.getItem('rima_port_3') || 'COM12',
  hostApi: localStorage.getItem('rima_host_api') || 'http://localhost:8000',
  simulationMode: localStorage.getItem('rima_simulation_mode') === null ? true : localStorage.getItem('rima_simulation_mode') === 'true',
  synthVolume: localStorage.getItem('rima_synth_volume') === null ? 0.7 : parseFloat(localStorage.getItem('rima_synth_volume')),
  physicalPower: localStorage.getItem('rima_physical_power') === null ? 100 : parseInt(localStorage.getItem('rima_physical_power')),
  v1Volume: localStorage.getItem('rima_v1_volume') === null ? 1.0 : parseFloat(localStorage.getItem('rima_v1_volume')),
  v2Volume: localStorage.getItem('rima_v2_volume') === null ? 0.18 : parseFloat(localStorage.getItem('rima_v2_volume')),
  vbVolume: localStorage.getItem('rima_vb_volume') === null ? 0.25 : parseFloat(localStorage.getItem('rima_vb_volume')),
  vaVolume: localStorage.getItem('rima_va_volume') === null ? 0.06 : parseFloat(localStorage.getItem('rima_va_volume')),
  v1Staccato: localStorage.getItem('rima_v1_staccato') === 'true',
  v2Staccato: localStorage.getItem('rima_v2_staccato') === 'true',
  vbStaccato: localStorage.getItem('rima_vb_staccato') === 'true',
  vaStaccato: localStorage.getItem('rima_va_staccato') === 'true'
};

// 2. Song Database (Loaded Dynamically)
let songs = [];

// Web Audio API Synthesizer for Angklung
const NOTE_FREQUENCIES = {
  1: { // Angklung 1 (High/Yellow)
    1: 392.00, 2: 440.00, 3: 466.16, 4: 493.88, 5: 523.25, 6: 587.33, 7: 659.25, 8: 698.46,
    9: 739.99, 10: 783.99, 11: 880.00, 12: 932.33, 13: 987.77, 14: 1046.50, 15: 1174.66, 16: 1318.51
  },
  2: { // Angklung 2 (Medium/Green)
    1: 349.23, 2: 369.99, 3: 415.30, 4: 554.37, 5: 622.25, 6: 830.61, 7: 1109.73, 8: 1244.51,
    9: 1396.91, 10: 1479.98, 11: 1567.98, 12: 1661.22, 13: 1760.00, 14: 1864.66, 15: 1975.53, 16: 2093.00
  },
  3: { // Angklung 3 (Low/Blue)
    1: 164.81, 2: 174.61, 3: 185.00, 4: 196.00, 5: 207.65, 6: 220.00, 7: 233.08, 8: 246.94,
    9: 261.63, 10: 277.18, 11: 293.66, 12: 311.13, 13: 329.63, 14: 349.23, 15: 369.99, 16: 392.00
  }
};

let audioCtx = null;

function getAudioContext() {
  if (!audioCtx) {
    audioCtx = new (window.AudioContext || window.webkitAudioContext)();
  }
  if (audioCtx.state === 'suspended') {
    audioCtx.resume();
  }
  return audioCtx;
}

function playClientSynthSound(frequency) {
  try {
    const ctx = getAudioContext();
    const now = ctx.currentTime;
    
    // Create master gain envelope
    const masterGain = ctx.createGain();
    masterGain.gain.setValueAtTime(0, now);
    masterGain.gain.linearRampToValueAtTime(0.7, now + 0.015);
    masterGain.gain.exponentialRampToValueAtTime(0.0001, now + 1.2);
    masterGain.connect(ctx.destination);
    
    // fundamental (f1)
    const osc1 = ctx.createOscillator();
    osc1.type = 'sine';
    osc1.frequency.setValueAtTime(frequency, now);
    
    const gain1 = ctx.createGain();
    gain1.gain.setValueAtTime(0.5, now);
    gain1.gain.exponentialRampToValueAtTime(0.0001, now + 1.0);
    
    osc1.connect(gain1);
    gain1.connect(masterGain);
    
    // octave (2f)
    const osc2 = ctx.createOscillator();
    osc2.type = 'sine';
    osc2.frequency.setValueAtTime(frequency * 2.0, now);
    
    const gain2 = ctx.createGain();
    gain2.gain.setValueAtTime(0.4, now);
    gain2.gain.exponentialRampToValueAtTime(0.0001, now + 1.2);
    
    osc2.connect(gain2);
    gain2.connect(masterGain);
    
    // 3rd harmonic (3f)
    const osc3 = ctx.createOscillator();
    osc3.type = 'sine';
    osc3.frequency.setValueAtTime(frequency * 3.0, now);
    
    const gain3 = ctx.createGain();
    gain3.gain.setValueAtTime(0.1, now);
    gain3.gain.exponentialRampToValueAtTime(0.0001, now + 0.6);
    
    osc3.connect(gain3);
    gain3.connect(masterGain);
    
    // Wooden strike (noise click)
    const bufferSize = ctx.sampleRate * 0.02;
    const buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate);
    const data = buffer.getChannelData(0);
    for (let i = 0; i < bufferSize; i++) {
      data[i] = (Math.random() * 2 - 1) * Math.exp(-i / (bufferSize * 0.2));
    }
    
    const noise = ctx.createBufferSource();
    noise.buffer = buffer;
    
    const noiseGain = ctx.createGain();
    noiseGain.gain.setValueAtTime(0.25, now);
    noiseGain.gain.exponentialRampToValueAtTime(0.0001, now + 0.02);
    
    noise.connect(noiseGain);
    noiseGain.connect(masterGain);
    
    osc1.start(now);
    osc2.start(now);
    osc3.start(now);
    noise.start(now);
    
    osc1.stop(now + 1.3);
    osc2.stop(now + 1.3);
    osc3.stop(now + 1.3);
    noise.stop(now + 0.05);
  } catch (e) {
    console.error("Gagal memutar audio Web Audio API:", e);
  }
}

function playSustainedSynthSound(frequency, durationMs = 300) {
  try {
    const ctx = getAudioContext();
    const now = ctx.currentTime;
    const durSec = Math.max(0.1, durationMs / 1000.0);

    const masterGain = ctx.createGain();
    masterGain.gain.setValueAtTime(0.0001, now);
    masterGain.gain.linearRampToValueAtTime(0.75, now + 0.025);
    
    const sustainEnd = Math.max(now + 0.03, now + durSec - 0.04);
    masterGain.gain.setValueAtTime(0.70, sustainEnd);
    masterGain.gain.linearRampToValueAtTime(0.0001, now + durSec);
    masterGain.connect(ctx.destination);

    // V1 Lead Vocal Fundamental (f1)
    const osc1 = ctx.createOscillator();
    osc1.type = 'sine';
    osc1.frequency.setValueAtTime(frequency, now);
    osc1.connect(masterGain);

    // V1 Octave Resonance (2f)
    const osc2 = ctx.createOscillator();
    osc2.type = 'sine';
    osc2.frequency.setValueAtTime(frequency * 2.0, now);
    const gain2 = ctx.createGain();
    gain2.gain.setValueAtTime(0.35, now);
    osc2.connect(gain2);
    gain2.connect(masterGain);

    // V1 Warm Vocal Overtone (3f)
    const osc3 = ctx.createOscillator();
    osc3.type = 'triangle';
    osc3.frequency.setValueAtTime(frequency * 3.0, now);
    const gain3 = ctx.createGain();
    gain3.gain.setValueAtTime(0.18, now);
    osc3.connect(gain3);
    gain3.connect(masterGain);

    osc1.start(now);
    osc2.start(now);
    osc3.start(now);

    osc1.stop(now + durSec);
    osc2.stop(now + durSec);
    osc3.stop(now + durSec);
  } catch (e) {
    console.error("Gagal memutar sustained synth sound:", e);
  }
}

let activeSongInterval = null;
let repeaterSocket = null;
let repeaterState = 'idle'; // 'idle', 'recording', 'playing'
let repeaterProcessingMode = 'soft'; // 'off', 'soft', 'hard'
let repeaterPlaybackInterval = null;
let recordedSequence = [];
let currentRecNote = null;
let currentRecStart = 0;
let currentRecDuration = 0;
let segNoteFrames = []; // {note, rms} semua frame dalam segmen nada yang sedang berjalan (persist antar start/stop)
let keyIntervals = new Map();
let chordIntervals = new Map();

// Robust Segment Label Picker — mencegah "nada akhir melengking naik/turun":
// alih-alih memakai activeNote APAPUN yang terjadi tepat saat segmen ditutup
// (bisa saja itu pitch-bend/napas-habis di ujung nada), pilih nada mayoritas
// HANYA dari frame-frame yang RMS-nya kuat (>= 50% puncak segmen) — yaitu bagian
// paling stabil & percaya diri dari nada tsb, mengabaikan wobble di attack/release.
function finalizeSegmentNote(frames) {
  if (!frames || frames.length === 0) return null;
  const maxRms = Math.max(...frames.map(f => f.rms), 0.0001);
  const strong = frames.filter(f => f.rms >= 0.5 * maxRms && f.note !== null);
  const pool = strong.length > 0 ? strong : frames.filter(f => f.note !== null);
  if (pool.length === 0) return null;
  const counts = {};
  for (const f of pool) counts[f.note] = (counts[f.note] || 0) + 1;
  let best = pool[pool.length - 1].note;
  let bestCount = 0;
  for (const note in counts) {
    if (counts[note] > bestCount) { bestCount = counts[note]; best = note; }
  }
  return best;
}

function setRepeaterMode(mode) {
  repeaterProcessingMode = mode;
  const softBtn = document.getElementById('mode-soft-btn');
  const hardBtn = document.getElementById('mode-hard-btn');
  const desc = document.getElementById('repeater-mode-desc');

  const btnReset = (btn) => {
    if (btn) {
      btn.style.background = 'transparent';
      btn.style.color = '#2E7D32';
      btn.style.boxShadow = 'none';
    }
  };
  const btnActive = (btn) => {
    if (btn) {
      btn.style.background = '#2E7D32';
      btn.style.color = '#FFFFFF';
      btn.style.boxShadow = '0 2px 6px rgba(46,125,50,0.3)';
    }
  };

  btnReset(softBtn);
  btnReset(hardBtn);

  if (mode === 'soft') {
    btnActive(softBtn);
    if (desc) desc.textContent = 'Mode Presisi (Lagu Asli): Presisi nada lagu asli 100% akurat dengan toleransi halus.';
  } else if (mode === 'hard') {
    btnActive(hardBtn);
    if (desc) desc.textContent = 'Mode Auto-Tune (Vokal Awam): Mengoreksi 100% vokal awam yang fals ke tangga nada terdekat.';
  }
}

function isMidiInputPlayAllowed() {
  return appCurrentPage === 'page-manual' || appCurrentPage === 'page-gamenotangka' || appCurrentPage === 'page-notangka';
}

function startKeyTrigger(keyElement) {
  if (!isMidiInputPlayAllowed()) return;
  const noteId = `${keyElement.getAttribute('data-angklung')}-${keyElement.getAttribute('data-note')}`;
  
  // Prevent duplicate triggers if already held
  if (keyIntervals.has(noteId)) return;
  
  // Add active visual immediately
  keyElement.classList.add('active');
  
  // Function to perform a single strike/shake trigger
  const triggerStrike = () => {
    const noteNum = parseInt(keyElement.getAttribute('data-note'), 10);
    const label = keyElement.getAttribute('data-label');
    const angklungId = parseInt(keyElement.getAttribute('data-angklung') || '3', 10);
    
    const activeDisplay = document.getElementById('active-note-display');
    if (activeDisplay) activeDisplay.textContent = label.toUpperCase();
    const indicatorContainer = document.getElementById('notes-indicator-container');
    if (indicatorContainer) indicatorContainer.style.opacity = '1';

    // Play local synthesizer sound
    const freqMap = NOTE_FREQUENCIES[angklungId];
    if (freqMap && freqMap[noteNum]) {
      playClientSynthSound(freqMap[noteNum]);
    }

    // Send to python serial endpoint
    fetch(`${settings.hostApi}/api/arduino/play?note=${noteNum}&angklung_id=${angklungId}`).catch(() => {});
  };
  
  // Initial trigger
  triggerStrike();
  
  // Set interval for continuous shaking/tremolo (every 160ms)
  const intervalId = setInterval(triggerStrike, 160);
  keyIntervals.set(noteId, intervalId);
}

function stopKeyTrigger(keyElement) {
  const noteId = `${keyElement.getAttribute('data-angklung')}-${keyElement.getAttribute('data-note')}`;
  if (keyIntervals.has(noteId)) {
    clearInterval(keyIntervals.get(noteId));
    keyIntervals.delete(noteId);
  }
  keyElement.classList.remove('active');
}

// Map MIDI note number (60-72, C4 to C5) to Not Angka scale degree string
function mapMidiNoteToScaleDegree(midiNote) {
  const noteVal = parseInt(midiNote, 10);
  if (isNaN(noteVal)) return null;

  const directMap = {
    59: "0",  // B3 = Si Rendah (0)
    60: "1",  // C4 = Do
    62: "2",  // D4 = Re
    64: "3",  // E4 = Mi
    65: "4",  // F4 = Fa
    67: "5",  // G4 = Sol
    69: "6",  // A4 = La
    71: "7",  // B4 = Si
    72: "1'" // C5 = Do Tinggi
  };
  if (directMap[noteVal]) return directMap[noteVal];

  // Octave-independent pitch class mapping (C = 0)
  const pitchClass = noteVal % 12;
  const pcMap = { 0: "1", 2: "2", 4: "3", 5: "4", 7: "5", 9: "6", 11: "7" };
  if (pitchClass === 11 && noteVal < 60) return "0";
  if (pitchClass === 0 && noteVal >= 72) return "1'";
  return pcMap[pitchClass] || null;
}

// Map Angklung Hardware Note (1-16) to Not Angka scale degree string
function mapAngklungNoteToScaleDegree(noteNum, angklungId = 3) {
  const noteVal = parseInt(noteNum, 10);
  if (isNaN(noteVal)) return null;

  // Angklung 3 (BASS_PITCHES) mapping:
  // e3(1), f3(2), f#3(3), g3(4), g#3(5), a3(6), a#3(7), b3(8),
  // c4(9), c#4(10), d4(11), d#4(12), e4(13), f4(14), f#4(15), g4(16)
  if (angklungId === 3) {
    const bassMap = {
      8: "0",   // b3  = Si Rendah (0)
      9: "1",   // c4  = Do
      10: "1",  // c#4 = Do
      11: "2",  // d4  = Re
      12: "2",  // d#4 = Re
      13: "3",  // e4  = Mi
      14: "4",  // f4  = Fa
      15: "4",  // f#4 = Fa
      16: "5"   // g4  = Sol
    };
    if (bassMap[noteVal]) return bassMap[noteVal];
  }

  // Angklung 1 (ANGKLUNG1_PITCHES) mapping:
  // g4(1), a4(2), a#4(3), b4(4), c5(5), d5(6), e5(7), f5(8) ...
  if (angklungId === 1) {
    const a1Map = {
      1: "5",   // g4  = Sol
      2: "6",   // a4  = La
      4: "7",   // b4  = Si
      5: "1'",  // c5  = Do Tinggi
      6: "2'",  // d5
      7: "3'",  // e5
      8: "4'"   // f5
    };
    if (a1Map[noteVal]) return a1Map[noteVal];
  }

  // Standard 1-8 fallback mapping:
  const angklungMap = {
    1: "1",
    2: "2",
    3: "3",
    4: "4",
    5: "5",
    6: "6",
    7: "7",
    8: "1'"
  };
  return angklungMap[noteVal] || null;
}

let midiSocket = null;

function setKeyProgrammaticState(noteNum, angklungId, isDown, midiNote = null) {
  // Gating: Only allow MIDI audio/visual triggers on Kontrol Manual and Game Lagu pages
  if (!isMidiInputPlayAllowed()) return;

  const key = document.querySelector(`.key[data-note="${noteNum}"][data-angklung="${angklungId}"]`);
  if (key) {
    if (isDown) {
      key.classList.add('active');
      const activeDisplay = document.getElementById('active-note-display');
      if (activeDisplay) activeDisplay.textContent = key.getAttribute('data-label').toUpperCase();
      const indicatorContainer = document.getElementById('notes-indicator-container');
      if (indicatorContainer) indicatorContainer.style.opacity = '1';
      
      const freqMap = NOTE_FREQUENCIES[angklungId];
      if (freqMap && freqMap[noteNum]) {
        playClientSynthSound(freqMap[noteNum]);
      }
    } else {
      key.classList.remove('active');
    }
  }

  // GAME LAGU MIDI INTEGRATION: Automatically advance Game Not Angka on physical MIDI keyboard note press
  if (isDown && appCurrentPage === 'page-gamenotangka') {
    let scaleDegree = null;
    if (midiNote) {
      scaleDegree = mapMidiNoteToScaleDegree(midiNote);
    }
    if (!scaleDegree) {
      scaleDegree = mapAngklungNoteToScaleDegree(noteNum, angklungId);
    }
    if (scaleDegree) {
      onGameKeypadPress(scaleDegree);
    }
  }
}

function connectMidiWebSocket() {
  const wsHost = settings.hostApi.replace('http://', 'ws://').replace('https://', 'wss://');
  
  if (midiSocket) {
    try { midiSocket.close(); } catch (_) {}
  }

  midiSocket = new WebSocket(`${wsHost}/ws/midi`);

  midiSocket.onopen = () => {
    console.log("[WS-MIDI] Terhubung ke feedback tuts MIDI.");
  };

  midiSocket.onmessage = (event) => {
    if (!isMidiInputPlayAllowed()) return;
    try {
      const data = JSON.parse(event.data);
      if (data.note && data.angklung) {
        const isDown = data.action === "down" || data.action === "play" || !data.action;
        setKeyProgrammaticState(data.note, data.angklung, isDown, data.midi || data.midi_note);
      }
    } catch (e) {
      console.error("[WS-MIDI] Error parsing message:", e);
    }
  };

  midiSocket.onclose = () => {
    console.log("[WS-MIDI] Sambungan terputus. Mencoba menghubungkan kembali dalam 5 detik...");
    setTimeout(connectMidiWebSocket, 5000);
  };

  midiSocket.onerror = (err) => {
    console.error("[WS-MIDI] WebSocket error:", err);
  };
}

// 3. Application Startup
document.addEventListener('DOMContentLoaded', () => {
  // Auto-hide Splash Screen after 2.5 seconds
  setTimeout(() => {
    if (document.getElementById('page-landing').classList.contains('active')) {
      navigateTo('page-beranda');
    }
  }, 2500);

  // Set initial settings values to modal inputs
  scanComPorts();
  initDraggableAgenRimaFab();
  document.getElementById('input-com-port-2').value = "Terintegrasi dengan Angklung 1";
  document.getElementById('input-host-api').value = settings.hostApi;

  // Initialize view and run background connection checks
  loadSongsFromBackend();
  checkConnections();
  setInterval(checkConnections, 6000); // Check connections every 6 seconds

  // Auto-connect last MIDI device on startup
  const savedMidiId = localStorage.getItem('rima_midi_device_id');
  if (savedMidiId && savedMidiId !== 'null' && savedMidiId !== 'undefined') {
    const parsedId = parseInt(savedMidiId, 10);
    if (!isNaN(parsedId)) {
      fetch(`${settings.hostApi}/api/midi/connect`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ device_id: parsedId })
      }).catch(() => {});
    }
  }

  // Connect to MIDI feedback WebSocket
  connectMidiWebSocket();

  // Initialize volume settings on backend
  fetch(`${settings.hostApi}/api/arduino/volume`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      synth_volume: settings.synthVolume,
      physical_power: settings.physicalPower,
      v1_volume: settings.v1Volume,
      v2_volume: settings.v2Volume,
      vb_volume: settings.vbVolume,
      va_volume: settings.vaVolume,
      v1_staccato: settings.v1Staccato,
      v2_staccato: settings.v2Staccato,
      vb_staccato: settings.vbStaccato,
      va_staccato: settings.vaStaccato
    })
  }).catch(() => {});




  // Track global mouse state for slide-to-play
  let isMouseDown = false;
  window.addEventListener('mousedown', () => { isMouseDown = true; });
  window.addEventListener('mouseup', () => { isMouseDown = false; });

  // Attach Piano Keys Interaction listeners
  const keys = document.querySelectorAll('.key');
  keys.forEach(key => {
    // Mouse interaction
    key.addEventListener('mousedown', (e) => {
      e.preventDefault();
      startKeyTrigger(key);
    });
    
    key.addEventListener('mouseenter', () => {
      if (isMouseDown) {
        startKeyTrigger(key);
      }
    });

    key.addEventListener('mouseup', () => stopKeyTrigger(key));
    key.addEventListener('mouseleave', () => stopKeyTrigger(key));
    
    // Touch interaction (Slide/drag on touch screen)
    key.addEventListener('touchstart', (e) => {
      e.preventDefault();
      startKeyTrigger(key);
    });
    
    key.addEventListener('touchmove', (e) => {
      e.preventDefault();
      const touch = e.touches[0];
      const targetElement = document.elementFromPoint(touch.clientX, touch.clientY);
      if (targetElement && targetElement.classList.contains('key')) {
        // Stop other active touch triggers
        keys.forEach(k => {
          if (k !== targetElement) stopKeyTrigger(k);
        });
        startKeyTrigger(targetElement);
      }
    });

    key.addEventListener('touchend', () => stopKeyTrigger(key));
    key.addEventListener('touchcancel', () => stopKeyTrigger(key));
  });

  // Attach Chord Buttons Interaction listeners
  const chordBtns = document.querySelectorAll('.chord-btn');
  chordBtns.forEach(btn => {
    const chordName = btn.getAttribute('data-chord');
    
    // Mouse interaction
    btn.addEventListener('mousedown', (e) => {
      e.preventDefault();
      startChordTrigger(chordName, btn);
    });
    btn.addEventListener('mouseenter', () => {
      if (isMouseDown) {
        startChordTrigger(chordName, btn);
      }
    });
    btn.addEventListener('mouseup', () => stopChordTrigger(chordName, btn));
    btn.addEventListener('mouseleave', () => stopChordTrigger(chordName, btn));
    
    // Touch interaction
    btn.addEventListener('touchstart', (e) => {
      e.preventDefault();
      startChordTrigger(chordName, btn);
    });
    btn.addEventListener('touchmove', (e) => {
      e.preventDefault();
      const touch = e.touches[0];
      const targetElement = document.elementFromPoint(touch.clientX, touch.clientY);
      if (targetElement && targetElement.classList.contains('chord-btn')) {
        chordBtns.forEach(b => {
          if (b !== targetElement) stopChordTrigger(b.getAttribute('data-chord'), b);
        });
        startChordTrigger(targetElement.getAttribute('data-chord'), targetElement);
      }
    });
    btn.addEventListener('touchend', () => stopChordTrigger(chordName, btn));
    btn.addEventListener('touchcancel', () => stopChordTrigger(chordName, btn));
  });
});

// 4. SPA Page Router
let appNavigationHistory = [];
let appCurrentPage = 'page-landing';

const PAGE_NAMES = {
  'page-pustaka': 'Pustaka Lagu',
  'page-manual': 'Kontrol Manual',
  'page-repeater': 'Repeater',
  'page-bahasa': 'Deteksi Bahasa',
  'page-pengenalan': 'Pengenalan Angklung',
  'page-notangka': 'Game Not Angka'
};

function navigateBack() {
  if (appNavigationHistory.length > 0) {
    const prevPage = appNavigationHistory.pop();
    navigateTo(prevPage, true);
    return prevPage;
  }
  return null;
}

function navigateTo(pageId, skipHistory = false) {
  if (!skipHistory && appCurrentPage !== pageId && appCurrentPage !== 'page-landing') {
     appNavigationHistory.push(appCurrentPage);
  }
  appCurrentPage = pageId;
  // Clear any running song playbacks or socket connections when switching pages
  stopAllPlaybacks();
  if (pageId !== 'page-bahasa') {
    setBahasaMode(1);
  }

  const agenRimaFab = document.getElementById('agen-rima-fab-container');
  if (agenRimaFab) {
    if (pageId === 'page-landing') {
      agenRimaFab.style.display = 'none';
    } else {
      agenRimaFab.style.display = 'block';
    }
  }

  // Hide all screens and activate selected
  const pages = document.querySelectorAll('.app-page');
  pages.forEach(page => page.classList.remove('active'));
  
  const targetPage = document.getElementById(pageId);
  if (targetPage) {
    targetPage.classList.add('active');
  }

  // Load songs dynamically when visiting the library page
  if (pageId === 'page-pustaka') {
    loadSongsFromBackend();
  }
}

// 5. Connection Diagnostics
async function checkConnections() {
  const host = settings.hostApi;

  // Check python FastAPI status
  let isApiOnline = false;
  try {
    const response = await fetch(`${host}/api/health`, { method: 'GET' });
    if (response.ok) isApiOnline = true;
  } catch (_) {}

  // Check serial com status on python for all 3 devices
  let statuses = { angklung1: 'offline', angklung2: 'offline', angklung3: 'offline' };
  let isMidiActive = false;
  let midiDeviceName = '';

  if (isApiOnline) {
    try {
      // Sync configurations to python backend
      await fetch(`${host}/api/config-arduino`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          port1: settings.port1,
          port2: settings.port2,
          port3: settings.port3,
          simulation_mode: settings.simulationMode
        })
      });

      const response = await fetch(`${host}/api/arduino/status`);
      if (response.ok) {
        const data = await response.json();
        statuses.angklung1 = data.angklung1.status;
        statuses.angklung2 = data.angklung2.status;
        statuses.angklung3 = data.angklung3.status;
      }
    } catch (_) {}

    try {
      const response = await fetch(`${host}/api/midi/status`);
      if (response.ok) {
        const data = await response.json();
        isMidiActive = data.active;
        midiDeviceName = data.device_name;
      }
    } catch (_) {}
  }

  // Update Status UI badges in Modal
  updateBadge('modal-api-status', isApiOnline);
  if (settings.simulationMode) {
    updateBadge('modal-serial-status-1', true, 'Simulasi (Aktif)');
    updateBadge('modal-serial-status-2', true, 'Simulasi (Aktif)');
    updateBadge('modal-serial-status-3', true, 'Simulasi (Aktif)');
  } else {
    updateBadge('modal-serial-status-1', statuses.angklung1 === 'online');
    updateBadge('modal-serial-status-2', statuses.angklung2 === 'online');
    updateBadge('modal-serial-status-3', statuses.angklung3 === 'online');
  }

  updateBadge('modal-midi-status', isMidiActive, isMidiActive ? `Terhubung (${midiDeviceName})` : 'Offline');
}


function updateBadge(id, isOnline, customText = null) {
  const badge = document.getElementById(id);
  if (badge) {
    if (isOnline) {
      badge.textContent = customText || 'Connected';
      badge.className = 'badge badge-green';
      if (customText) {
        badge.style.backgroundColor = '#0284c7'; // Sky blue for simulation
        badge.style.borderColor = '#0284c7';
      } else {
        badge.style.backgroundColor = '';
        badge.style.borderColor = '';
      }
    } else {
      badge.textContent = 'Offline';
      badge.className = 'badge badge-red';
      badge.style.backgroundColor = '';
      badge.style.borderColor = '';
    }
  }
}

let currentActiveConnectedMidiId = localStorage.getItem('rima_midi_device_id') || null;

// Settings Overlay Handlers
async function scanMidiDevices() {
  const host = settings.hostApi;
  const select = document.getElementById('select-midi-device');
  if (!select) return;

  const prevVal = select.value || currentActiveConnectedMidiId || localStorage.getItem('rima_midi_device_id') || "";

  try {
    const response = await fetch(`${host}/api/midi/devices`);
    if (response.ok) {
      const devices = await response.json();
      
      select.innerHTML = '<option value="">-- Scan/Pilih Keyboard MIDI --</option>';
      
      devices.forEach(device => {
        const option = document.createElement('option');
        option.value = device.id.toString();
        option.textContent = `${device.name} (${device.interface})`;
        select.appendChild(option);
      });
      
      // Sync active MIDI status from server
      try {
        const statusRes = await fetch(`${host}/api/midi/status`);
        if (statusRes.ok) {
          const statusData = await statusRes.json();
          if (statusData.active && statusData.device_id !== null && statusData.device_id !== undefined) {
            currentActiveConnectedMidiId = statusData.device_id.toString();
            localStorage.setItem('rima_midi_device_id', currentActiveConnectedMidiId);
          }
        }
      } catch (_) {}

      const valToSet = currentActiveConnectedMidiId || prevVal;
      if (valToSet !== null && valToSet !== undefined && devices.some(d => d.id.toString() === valToSet.toString())) {
        select.value = valToSet.toString();
      } else if (prevVal && devices.some(d => d.id.toString() === prevVal.toString())) {
        select.value = prevVal.toString();
      }
    }
  } catch (err) {
    console.error("Gagal melakukan scan perangkat MIDI:", err);
  }
}

async function scanComPorts() {
  const host = settings.hostApi;
  const select1 = document.getElementById('input-com-port-1');
  const select3 = document.getElementById('input-com-port-3');
  if (!select1 || !select3) return;

  const currentP1 = settings.port1 || select1.value || "COM10";
  const currentP3 = settings.port3 || select3.value || "COM12";

  let availablePorts = [];
  try {
    const res = await fetch(`${host}/api/ports`);
    if (res.ok) {
      availablePorts = await res.json();
    }
  } catch (err) {
    console.warn("Gagal scan COM ports dari API:", err);
  }

  const portSet = new Set();
  availablePorts.forEach(p => {
    if (p.port) portSet.add(p.port);
  });

  if (currentP1) portSet.add(currentP1);
  if (currentP3) portSet.add(currentP3);

  // Standard fallback COM ports
  for (let i = 1; i <= 20; i++) {
    portSet.add(`COM${i}`);
  }

  const sortedPorts = Array.from(portSet).sort((a, b) => {
    const numA = parseInt(a.replace(/\D/g, ''), 10) || 0;
    const numB = parseInt(b.replace(/\D/g, ''), 10) || 0;
    return numA - numB;
  });

  select1.innerHTML = '';
  sortedPorts.forEach(port => {
    const opt = document.createElement('option');
    opt.value = port;
    const detInfo = availablePorts.find(p => p.port === port);
    opt.textContent = detInfo ? `${port} (${detInfo.description})` : port;
    select1.appendChild(opt);
  });
  select1.value = currentP1;

  select3.innerHTML = '';
  sortedPorts.forEach(port => {
    const opt = document.createElement('option');
    opt.value = port;
    const detInfo = availablePorts.find(p => p.port === port);
    opt.textContent = detInfo ? `${port} (${detInfo.description})` : port;
    select3.appendChild(opt);
  });
  select3.value = currentP3;
}

function switchSettingTab(tabId) {
  // Update buttons
  document.getElementById('tab-btn-koneksi').classList.remove('active');
  document.getElementById('tab-btn-volume').classList.remove('active');
  document.getElementById(`tab-btn-${tabId}`).classList.add('active');

  // Update content visibility
  document.getElementById('tab-koneksi').style.display = (tabId === 'koneksi') ? 'block' : 'none';
  document.getElementById('tab-volume').style.display = (tabId === 'volume') ? 'block' : 'none';
}

async function toggleSettingsModal() {
  const modal = document.getElementById('settings-modal');
  if (!modal.classList.contains('active')) {
    await scanComPorts();
    document.getElementById('input-com-port-1').value = settings.port1;
    document.getElementById('input-com-port-3').value = settings.port3;
    document.getElementById('input-host-api').value = settings.hostApi;
    document.getElementById('input-simulation-mode').checked = settings.simulationMode;
    document.getElementById('input-synth-volume').value = Math.round(settings.synthVolume * 100);
    document.getElementById('input-physical-volume').value = settings.physicalPower;
    document.getElementById('input-volume-v1').value = Math.round(settings.v1Volume * 100);
    document.getElementById('input-volume-v2').value = Math.round(settings.v2Volume * 100);
    document.getElementById('input-volume-vb').value = Math.round(settings.vbVolume * 100);
    document.getElementById('input-volume-va').value = Math.round(settings.vaVolume * 100);
    
    document.getElementById('input-staccato-v1').checked = settings.v1Staccato;
    document.getElementById('input-staccato-v2').checked = settings.v2Staccato;
    document.getElementById('input-staccato-vb').checked = settings.vbStaccato;
    document.getElementById('input-staccato-va').checked = settings.vaStaccato;
    
    updateVolumeLabels();
    
    await scanMidiDevices();
  }
  modal.classList.toggle('active');
  const playerPanel = document.getElementById('global-player-panel');
  if (playerPanel) {
    if (modal.classList.contains('active')) {
      playerPanel.style.zIndex = '1';
    } else {
      playerPanel.style.zIndex = '';
    }
  }
}

function updateVolumeLabels() {
  const synthVal = document.getElementById('input-synth-volume').value;
  const physVal = document.getElementById('input-physical-volume').value;
  const v1Val = document.getElementById('input-volume-v1').value;
  const v2Val = document.getElementById('input-volume-v2').value;
  const vbVal = document.getElementById('input-volume-vb').value;
  const vaVal = document.getElementById('input-volume-va').value;

  document.getElementById('label-synth-volume').innerText = `${synthVal}%`;
  document.getElementById('label-physical-volume').innerText = `${physVal}%`;
  document.getElementById('label-volume-v1').innerText = `${v1Val}%`;
  document.getElementById('label-volume-v2').innerText = `${v2Val}%`;
  document.getElementById('label-volume-vb').innerText = `${vbVal}%`;
  document.getElementById('label-volume-va').innerText = `${vaVal}%`;
}

async function saveConnectionSettings() {
  const p1 = document.getElementById('input-com-port-1').value.trim();
  const p3 = document.getElementById('input-com-port-3').value.trim();
  const hostVal = document.getElementById('input-host-api').value.trim();
  const simMode = document.getElementById('input-simulation-mode').checked;
  const synthVolVal = parseFloat(document.getElementById('input-synth-volume').value) / 100;
  const physVolVal = parseInt(document.getElementById('input-physical-volume').value);
  
  const v1VolVal = parseFloat(document.getElementById('input-volume-v1').value) / 100;
  const v2VolVal = parseFloat(document.getElementById('input-volume-v2').value) / 100;
  const vbVolVal = parseFloat(document.getElementById('input-volume-vb').value) / 100;
  const vaVolVal = parseFloat(document.getElementById('input-volume-va').value) / 100;
  
  const v1StacVal = document.getElementById('input-staccato-v1').checked;
  const v2StacVal = document.getElementById('input-staccato-v2').checked;
  const vbStacVal = document.getElementById('input-staccato-vb').checked;
  const vaStacVal = document.getElementById('input-staccato-va').checked;
  
  const selectMidi = document.getElementById('select-midi-device');

  settings.port1 = p1;
  settings.port2 = p1; // Share same port with Angklung 1
  settings.port3 = p3;
  settings.hostApi = hostVal;
  settings.simulationMode = simMode;
  settings.synthVolume = synthVolVal;
  settings.physicalPower = physVolVal;
  settings.v1Volume = v1VolVal;
  settings.v2Volume = v2VolVal;
  settings.vbVolume = vbVolVal;
  settings.vaVolume = vaVolVal;
  settings.v1Staccato = v1StacVal;
  settings.v2Staccato = v2StacVal;
  settings.vbStaccato = vbStacVal;
  settings.vaStaccato = vaStacVal;

  localStorage.setItem('rima_port_1', p1);
  localStorage.setItem('rima_port_2', p1);
  localStorage.setItem('rima_port_3', p3);
  localStorage.setItem('rima_host_api', hostVal);
  localStorage.setItem('rima_simulation_mode', simMode);
  localStorage.setItem('rima_synth_volume', synthVolVal);
  localStorage.setItem('rima_physical_power', physVolVal);
  localStorage.setItem('rima_v1_volume', v1VolVal);
  localStorage.setItem('rima_v2_volume', v2VolVal);
  localStorage.setItem('rima_vb_volume', vbVolVal);
  localStorage.setItem('rima_va_volume', vaVolVal);
  localStorage.setItem('rima_v1_staccato', v1StacVal);
  localStorage.setItem('rima_v2_staccato', v2StacVal);
  localStorage.setItem('rima_vb_staccato', vbStacVal);
  localStorage.setItem('rima_va_staccato', vaStacVal);

  // Send volume settings to API
  try {
    await fetch(`${hostVal}/api/arduino/volume`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        synth_volume: synthVolVal,
        physical_power: physVolVal,
        v1_volume: v1VolVal,
        v2_volume: v2VolVal,
        vb_volume: vbVolVal,
        va_volume: vaVolVal,
        v1_staccato: v1StacVal,
        v2_staccato: v2StacVal,
        vb_staccato: vbStacVal,
        va_staccato: vaStacVal
      })
    });
  } catch (err) {
    console.error("Gagal mengirim pengaturan volume:", err);
  }

  // Connect or disconnect MIDI device ONLY if user explicitly changed the selection!
  if (selectMidi) {
    const selectedVal = selectMidi.value;
    const activeVal = currentActiveConnectedMidiId ? currentActiveConnectedMidiId.toString() : "";

    if (selectedVal !== activeVal) {
      if (selectedVal !== "") {
        const deviceId = parseInt(selectedVal, 10);
        try {
          await fetch(`${hostVal}/api/midi/connect`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ device_id: deviceId })
          });
          currentActiveConnectedMidiId = selectedVal;
          localStorage.setItem('rima_midi_device_id', selectedVal);
        } catch (e) {
          console.error("Gagal menyambung MIDI:", e);
        }
      } else if (activeVal !== "") {
        // Only disconnect if there was an active MIDI device and the user explicitly chose "-- Scan/Pilih Keyboard MIDI --"
        try {
          await fetch(`${hostVal}/api/midi/disconnect`, { method: 'POST' });
          currentActiveConnectedMidiId = null;
          localStorage.removeItem('rima_midi_device_id');
        } catch (_) {}
      }
    }
  }

  toggleSettingsModal();
  checkConnections();
}


// 6. Interactive Keyboard Playback
function triggerKeyOn(keyElement) {
  if (!isMidiInputPlayAllowed()) return;

  const noteNum = parseInt(keyElement.getAttribute('data-note'), 10);
  const label = keyElement.getAttribute('data-label');
  const angklungId = parseInt(keyElement.getAttribute('data-angklung') || '3', 10);
  
  // Show active visual trigger
  keyElement.classList.add('active');
  const activeDisplay = document.getElementById('active-note-display');
  if (activeDisplay) activeDisplay.textContent = label.toUpperCase();
  const indicatorContainer = document.getElementById('notes-indicator-container');
  if (indicatorContainer) indicatorContainer.style.opacity = '1';

  // Play client-side audio synth instantly
  const freqMap = NOTE_FREQUENCIES[angklungId];
  if (freqMap && freqMap[noteNum]) {
    playClientSynthSound(freqMap[noteNum]);
  }

  // Send request to python backend
  fetch(`${settings.hostApi}/api/arduino/play?note=${noteNum}&angklung_id=${angklungId}`).catch(() => {});

  // Remove active visual after transient delay
  setTimeout(() => {
    keyElement.classList.remove('active');
  }, 250);
}

// Programmatic key highlight (for repeater incoming feedback & song playbacks)
function highlightKeyProgrammatic(noteNum, angklungId = 3, playSound = true, durationMs = 250) {
  if (!isMidiInputPlayAllowed()) return;

  const key = document.querySelector(`.key[data-note="${noteNum}"][data-angklung="${angklungId}"]`);
  if (key) {
    key.classList.add('active');
    const activeDisplay = document.getElementById('active-note-display');
    if (activeDisplay) activeDisplay.textContent = key.getAttribute('data-label').toUpperCase();
    document.getElementById('notes-indicator-container').style.opacity = '1';
    
    if (playSound) {
      const freqMap = NOTE_FREQUENCIES[angklungId];
      if (freqMap && freqMap[noteNum]) {
        playSustainedSynthSound(freqMap[noteNum], durationMs);
      }
    }
    
    setTimeout(() => {
      key.classList.remove('active');
    }, Math.max(150, durationMs));
  }
}
// Map pitch names to physical hardware positions
const PITCH_TO_HARDWARE = {
  // Angklung 3 (Bass)
  "e3": { angklung: 3, note: 1 }, "f3": { angklung: 3, note: 2 }, "f#3": { angklung: 3, note: 3 },
  "g3": { angklung: 3, note: 4 }, "g#3": { angklung: 3, note: 5 }, "a3": { angklung: 3, note: 6 },
  "a#3": { angklung: 3, note: 7 }, "b3": { angklung: 3, note: 8 }, "c4": { angklung: 3, note: 9 },
  "c#4": { angklung: 3, note: 10 }, "d4": { angklung: 3, note: 11 }, "d#4": { angklung: 3, note: 12 },
  "e4": { angklung: 3, note: 13 }, "f4_bass": { angklung: 3, note: 14 }, "f#4_bass": { angklung: 3, note: 15 },
  "g4_bass": { angklung: 3, note: 16 },

  // Angklung 1 (High/Yellow) & Angklung 2 (Medium/Green)
  "f4": { angklung: 2, note: 1 }, "f#4": { angklung: 2, note: 2 }, "g4": { angklung: 1, note: 1 },
  "g#4": { angklung: 2, note: 3 }, "a4": { angklung: 1, note: 2 }, "a#4": { angklung: 1, note: 3 },
  "b4": { angklung: 1, note: 4 }, "c5": { angklung: 1, note: 5 }, "c#5": { angklung: 2, note: 4 },
  "d5": { angklung: 1, note: 6 }, "d#5": { angklung: 2, note: 5 }, "e5": { angklung: 1, note: 7 },
  "f5": { angklung: 1, note: 8 }, "f#5": { angklung: 1, note: 9 }, "g5": { angklung: 1, note: 10 },
  "g#5": { angklung: 2, note: 6 }, "a5": { angklung: 1, note: 11 }, "a#5": { angklung: 1, note: 12 },
  "b5": { angklung: 1, note: 13 }, "c6": { angklung: 1, note: 14 }, "c#6": { angklung: 2, note: 7 },
  "d6": { angklung: 1, note: 15 }, "d#6": { angklung: 2, note: 8 }, "e6": { angklung: 1, note: 16 },
  "f6": { angklung: 2, note: 9 }, "f#6": { angklung: 2, note: 10 }, "g6": { angklung: 2, note: 11 },
  "g#6": { angklung: 2, note: 12 }, "a6": { angklung: 2, note: 13 }, "a#6": { angklung: 2, note: 14 },
  "b6": { angklung: 2, note: 15 }, "c7": { angklung: 2, note: 16 }
};

function midiToPitchName(midi, preferBass) {
  const names = ["c", "c#", "d", "d#", "e", "f", "f#", "g", "g#", "a", "a#", "b"];
  const octave = Math.floor(midi / 12) - 1;
  const pitch = names[midi % 12] + octave;
  if (preferBass) {
    if (midi >= 52 && midi <= 67) {
      if (pitch === "f4") return "f4_bass";
      if (pitch === "f#4") return "f#4_bass";
      if (pitch === "g4") return "g4_bass";
      return pitch;
    }
  }
  return pitch;
}

// 7. Chord Triggering
function playChord(chordName) {
  let rootName = chordName;
  let suffix = '';
  
  // Extract root and suffix
  const regex = /^([A-G][#b]?)(.*)$/;
  const match = chordName.match(regex);
  if (!match) return;
  rootName = match[1];
  suffix = match[2];
  
  const rootMap = {
    'C': 60, 'C#': 61, 'Db': 61, 'D': 62, 'D#': 63, 'Eb': 63,
    'E': 64, 'F': 65, 'F#': 66, 'Gb': 66, 'G': 67, 'G#': 68,
    'Ab': 68, 'A': 69, 'A#': 70, 'Bb': 70, 'B': 71
  };
  
  const rootMidi = rootMap[rootName];
  if (!rootMidi) return;
  
  let offsets = [];
  if (suffix === '') offsets = [0, 4, 7]; // Major
  else if (suffix === 'm') offsets = [0, 3, 7]; // Minor
  else if (suffix === '7') offsets = [0, 4, 7, 10]; // Dominant 7th
  else if (suffix === 'maj7') offsets = [0, 4, 7, 11]; // Major 7th
  else if (suffix === 'm7') offsets = [0, 3, 7, 10]; // Minor 7th
  else if (suffix === 'dim') offsets = [0, 3, 6]; // Diminished
  else return; // Unsupported
  
  const melodyNotes = offsets.map(o => rootMidi + o);
  
  let bassMidi = rootMidi;
  while (bassMidi < 52) bassMidi += 12;
  while (bassMidi > 67) bassMidi -= 12;
  
  const resolvedKeys = [];
  
  const bassPitch = midiToPitchName(bassMidi, true);
  const bassHw = PITCH_TO_HARDWARE[bassPitch];
  if (bassHw) resolvedKeys.push(bassHw);
  
  melodyNotes.forEach(m => {
    let melMidi = m;
    while (melMidi < 65) melMidi += 12;
    while (melMidi > 92) melMidi -= 12;
    
    const melPitch = midiToPitchName(melMidi, false);
    const melHw = PITCH_TO_HARDWARE[melPitch];
    if (melHw) {
      if (!resolvedKeys.some(k => k.angklung === melHw.angklung && k.note === melHw.note)) {
        resolvedKeys.push(melHw);
      }
    }
  });

  document.getElementById('active-note-display').textContent = chordName;
  document.getElementById('notes-indicator-container').style.opacity = '1';

  const arduino1Notes = [];
  const arduino3Notes = [];

  resolvedKeys.forEach(k => {
    const keyEl = document.querySelector(`.key[data-note="${k.note}"][data-angklung="${k.angklung}"]`);
    if (keyEl) {
      keyEl.classList.add('active');
      setTimeout(() => keyEl.classList.remove('active'), 350);
    }
    
    const freqMap = NOTE_FREQUENCIES[k.angklung];
    if (freqMap && freqMap[k.note]) {
      playClientSynthSound(freqMap[k.note]);
    }
    
    if (k.angklung === 1) {
      arduino1Notes.push(k.note);
    } else if (k.angklung === 2) {
      arduino1Notes.push(k.note + 16);
    } else if (k.angklung === 3) {
      arduino3Notes.push(k.note);
    }
  });

  const a1Param = arduino1Notes.join(',');
  const a3Param = arduino3Notes.join(',');
  fetch(`${settings.hostApi}/api/arduino/play_multi?a1=${a1Param}&a3=${a3Param}`).catch(() => {});
}

function startChordTrigger(chordName, btnElement) {
  // Prevent duplicate trigger if already active
  if (chordIntervals.has(chordName)) return;

  // Add active style to chord button
  btnElement.classList.add('active');

  // Trigger chord once immediately
  playChord(chordName);

  // Repeat playChord every 160ms for tremolo/shaking effect on hold
  const intervalId = setInterval(() => {
    playChord(chordName);
  }, 160);
  
  chordIntervals.set(chordName, intervalId);
}

function stopChordTrigger(chordName, btnElement) {
  if (chordIntervals.has(chordName)) {
    clearInterval(chordIntervals.get(chordName));
    chordIntervals.delete(chordName);
  }
  btnElement.classList.remove('active');
}

// Helper to get safe DOM ID from song filename
function getSongBtnId(songId) {
  return 'btn-play-' + songId.replace(/[^a-zA-Z0-9]/g, '_');
}

// 8. Pustaka Lagu Section
let currentSongFilter = 'all';
let currentPlaylist = [];
let currentPlayingIndex = -1;
let isShuffle = false;
let isRepeat = false;
let isManualStop = false;
let isBahasaPlayback = false;

function searchSongs(query) {
  const container = document.getElementById('songs-container');
  if (!container) return;
  const lowerQuery = query.toLowerCase();
  
  const filtered = songs.filter(s => {
    const matchesFilter = currentSongFilter === 'all' || s.folder === currentSongFilter;
    const matchesSearch = s.title.toLowerCase().includes(lowerQuery) || (s.region && s.region.toLowerCase().includes(lowerQuery));
    return matchesFilter && matchesSearch;
  });
  
  currentPlaylist = filtered;
  renderSongCards(filtered, container);
}

// Category image mapping for Album Art cover
const categoryImageMap = {
  'aceh': 'aceh.jfif',
  'asia': 'asia.jfif',
  'barat': 'barat.jfif',
  'batak': 'batak.jfif',
  'daerah': 'daerah.jfif',
  'jawa': 'jawa.jfif',
  'kalimantan': 'kalimantan.jfif',
  'nasional': 'nasional.jfif',
  'papua': 'papua.jfif',
  'pop': 'pop.jfif',
  'religion': 'religion.jfif',
  'agama': 'religion.jfif',
  'rohani': 'religion.jfif',
  'sulawesi': 'sulawesi.jfif',
  'sunda': 'sunda.jfif'
};

function getCategoryImage(folderOrRegion) {
  if (!folderOrRegion) return 'daerah.jfif';
  const term = String(folderOrRegion).toLowerCase().trim();
  for (const [key, imgFile] of Object.entries(categoryImageMap)) {
    if (term.includes(key)) {
      return imgFile;
    }
  }
  return 'daerah.jfif';
}

function updateAlbumCoverImage(imagePath, titleText = null, descText = null) {
  const coverImg = document.getElementById('album-cover-img');
  if (coverImg) {
    coverImg.style.opacity = '0.3';
    setTimeout(() => {
      coverImg.src = imagePath;
      coverImg.style.opacity = '1';
    }, 150);
  }
  
  if (titleText !== null) {
    const titleEl = document.getElementById('current-album-title');
    if (titleEl) titleEl.innerText = titleText;
  }
  if (descText !== null) {
    const descEl = document.getElementById('current-album-desc');
    if (descEl) descEl.innerText = descText;
  }
}

function loadSongsList(filter = 'all') {
  currentSongFilter = filter;
  const container = document.getElementById('songs-container');
  if (!container) return;
  
  const searchInput = document.getElementById('cn-pustaka-search-input');
  const query = searchInput ? searchInput.value.toLowerCase() : '';

  const filtered = songs.filter(s => {
    const matchesFilter = filter === 'all' || s.folder === filter;
    const matchesSearch = !query || s.title.toLowerCase().includes(query) || (s.region && s.region.toLowerCase().includes(query));
    return matchesFilter && matchesSearch;
  });

  currentPlaylist = filtered;
  renderSongCards(filtered, container);

  // Update album cover based on selected category filter
  if (filter === 'all') {
    updateAlbumCoverImage('daerah.jfif', 'Pilih Lagu', 'Mainkan otomatis lagu daerah');
  } else {
    const catImg = getCategoryImage(filter);
    updateAlbumCoverImage(catImg, `Kategori: ${filter.toUpperCase()}`, `Koleksi Lagu ${filter.toUpperCase()}`);
  }
}

function renderSongCards(songArray, container) {
  container.innerHTML = '';
  
  songArray.forEach((song, index) => {
    const btnDomId = getSongBtnId(song.id);
    const item = document.createElement('div');
    item.className = 'song-item';
    item.id = `row-${btnDomId}`;
    item.onclick = () => playSong(song.id); // Klik baris memutar lagu
    item.innerHTML = `
      <div class="song-info">
        <span class="song-index">${index + 1}</span>
        <div class="song-details">
          <h4>${song.title}</h4>
          <p>${song.region} (${song.folder})</p>
        </div>
      </div>
      <span class="song-duration">${song.duration}</span>
      <button class="song-play-btn" id="${btnDomId}">
        <i class="fa-solid fa-play"></i>
      </button>
    `;
    container.appendChild(item);
  });
}

// Render dynamic filter options based on actual folders in the song library
function renderFilterTags() {
  const selectElement = document.getElementById('song-category-filter');
  if (!selectElement) return;
  selectElement.innerHTML = '';

  // 1. Add "SEMUA" option
  const allOpt = document.createElement('option');
  allOpt.value = 'all';
  allOpt.innerText = 'Semua Kategori';
  selectElement.appendChild(allOpt);

  // 2. Extract unique folder names and sort them
  const uniqueFolders = [...new Set(songs.map(s => s.folder))].filter(Boolean).sort();

  // 3. Render folder options
  uniqueFolders.forEach(folder => {
    const opt = document.createElement('option');
    opt.value = folder;
    opt.innerText = folder.toUpperCase();
    selectElement.appendChild(opt);
  });
}

// 8.1 Backend Song Loader
async function loadSongsFromBackend() {
  try {
    const response = await fetch(`${settings.hostApi}/api/songs`);
    if (response.ok) {
      const backendSongs = await response.json();
      songs = backendSongs.map(s => ({
        id: s.file_name,
        title: s.title,
        region: s.region,
        file_name: s.file_name,
        folder: s.folder || 'Umum',
        duration: s.duration || '0:00'
      }));
      renderFilterTags();
      loadSongsList('all');
    }
  } catch (e) {
    console.error("Gagal mengambil daftar lagu dari backend:", e);
  }
}

function filterSongs(category) {
  const buttons = document.querySelectorAll('.tag-btn');
  buttons.forEach(btn => btn.classList.remove('active'));
  if (window.event && window.event.target) {
    window.event.target.classList.add('active');
  }
  loadSongsList(category);
}

let playbackStatusInterval = null;

function startPlaybackStatusPolling() {
  if (playbackStatusInterval) clearInterval(playbackStatusInterval);
  
  playbackStatusInterval = setInterval(async () => {
    try {
      const response = await fetch(`${settings.hostApi}/api/arduino/playback_status`);
      if (response.ok) {
        const status = await response.json();
        if (status.active) {
          showPlayerPanel(status);
        } else {
          stopPlaybackStatusPolling();
          hidePlayerPanel();
          
          const playButtons = document.querySelectorAll('.song-play-btn');
          playButtons.forEach(btn => {
            btn.classList.remove('playing');
            btn.innerHTML = '<i class="fa-solid fa-play"></i>';
          });
          
          const globalPlayPauseIcon = document.getElementById('icon-playpause');
          if (globalPlayPauseIcon) {
            globalPlayPauseIcon.classList.remove('fa-pause');
            globalPlayPauseIcon.classList.add('fa-play');
          }
          
          if (isBahasaPlayback) {
            setBahasaMode(1);
          } else if (!isManualStop) {
            setTimeout(() => {
              playNextSong(true);
            }, 500);
          }
        }
      }
    } catch (e) {
      console.error("Gagal mendapatkan status playback:", e);
    }
  }, 250);
}

function stopPlaybackStatusPolling() {
  if (playbackStatusInterval) {
    clearInterval(playbackStatusInterval);
    playbackStatusInterval = null;
  }
}

function formatTime(seconds) {
  if (isNaN(seconds) || seconds === undefined) return '0:00';
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
}

function showPlayerPanel(status) {
  const panel = document.getElementById('global-player-panel');
  if (!panel) return;
  
  panel.classList.remove('hide');
  
  // Update UI texts
  const titleEl = document.getElementById('player-song-title');
  const sectionEl = document.getElementById('player-song-section');
  const elapsedEl = document.getElementById('player-time-elapsed');
  const totalEl = document.getElementById('player-time-total');
  const progressFill = document.getElementById('player-progress-fill');
  
  if (titleEl) titleEl.innerText = status.song_title || 'Unknown Song';
  if (sectionEl) {
    sectionEl.innerText = status.current_section || 'UMUM';
    // Hide section badge if it is default empty/UMUM
    if (status.current_section === 'UMUM' || !status.current_section) {
      sectionEl.style.display = 'none';
    } else {
      sectionEl.style.display = 'inline-block';
    }
  }
  if (elapsedEl) elapsedEl.innerText = formatTime(status.elapsed_seconds);
  if (totalEl) totalEl.innerText = formatTime(status.total_seconds);
  
  if (progressFill) {
    const percent = status.total_seconds > 0 ? (status.elapsed_seconds / status.total_seconds) * 100 : 0;
    progressFill.style.width = `${Math.min(percent, 100)}%`;
  }
}

function hidePlayerPanel() {
  const panel = document.getElementById('global-player-panel');
  if (panel) {
    panel.classList.add('hide');
  }
}

async function stopSongPlayback() {
  try {
    await fetch(`${settings.hostApi}/api/arduino/stop_song`);
  } catch (e) {
    console.error("Gagal menghentikan lagu:", e);
  }
  stopAllPlaybacks();
}

async function seekSong(event) {
  const rect = event.currentTarget.getBoundingClientRect();
  const clickX = event.clientX - rect.left;
  const width = rect.width;
  const percent = clickX / width;
  
  try {
    await fetch(`${settings.hostApi}/api/arduino/seek_song`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ percent: percent })
    });
  } catch (e) {
    console.error("Gagal melakukan seek lagu:", e);
  }
}

async function playSong(songId) {
  const playBtn = document.getElementById(getSongBtnId(songId));
  const songRow = document.getElementById(`row-${getSongBtnId(songId)}`);
  
  if (songRow && songRow.classList.contains('playing')) {
    isManualStop = true;
    stopAllPlaybacks();
    return;
  }

  isManualStop = false;
  currentPlayingIndex = currentPlaylist.findIndex(s => s.id === songId);
  
  stopAllPlaybacks();
  if (playBtn) {
    playBtn.innerHTML = '<i class="fa-solid fa-stop"></i>';
  }
  
  const globalPlayPauseIcon = document.getElementById('icon-playpause');
  if (globalPlayPauseIcon) {
    globalPlayPauseIcon.classList.remove('fa-play');
    globalPlayPauseIcon.classList.add('fa-pause');
  }

  if (songRow) {
    songRow.classList.add('playing');
    
    // Update Album Art image & info based on song category/region
    const songObj = songs.find(s => s.id === songId);
    const title = songRow.querySelector('h4') ? songRow.querySelector('h4').innerText : (songObj ? songObj.title : 'Sedang Memutar');
    const desc = songRow.querySelector('p') ? songRow.querySelector('p').innerText : (songObj ? `${songObj.region} (${songObj.folder})` : '');
    
    const catFolder = songObj ? (songObj.folder || songObj.region) : '';
    const catImg = getCategoryImage(catFolder);
    updateAlbumCoverImage(catImg, title, desc);
  }

  try {
    const response = await fetch(`${settings.hostApi}/api/arduino/play_song_file`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ file_name: songId })
    });
    if (response.ok) {
      startPlaybackStatusPolling();
    } else {
      alert("Gagal memutar lagu di server.");
      stopAllPlaybacks();
    }
  } catch (e) {
    alert("Gagal menghubungi server.");
    stopAllPlaybacks();
  }
}

// 8.2 Album Controls Functions
function playNextSong(autoPlay = false) {
  if (currentPlaylist.length === 0) return;
  
  let nextIndex = currentPlayingIndex;
  
  if (isRepeat && autoPlay) {
    nextIndex = currentPlayingIndex;
  } else if (isShuffle) {
    if (currentPlaylist.length > 1) {
      do {
        nextIndex = Math.floor(Math.random() * currentPlaylist.length);
      } while (nextIndex === currentPlayingIndex);
    } else {
      nextIndex = 0;
    }
  } else {
    nextIndex = currentPlayingIndex + 1;
    if (nextIndex >= currentPlaylist.length) {
      nextIndex = 0;
    }
  }
  
  const nextSong = currentPlaylist[nextIndex];
  if (nextSong) {
    playSong(nextSong.id);
  }
}

function playPrevSong() {
  if (currentPlaylist.length === 0) return;
  
  let prevIndex = currentPlayingIndex;
  
  if (isShuffle) {
    if (currentPlaylist.length > 1) {
      do {
        prevIndex = Math.floor(Math.random() * currentPlaylist.length);
      } while (prevIndex === currentPlayingIndex);
    } else {
      prevIndex = 0;
    }
  } else {
    prevIndex = currentPlayingIndex - 1;
    if (prevIndex < 0) {
      prevIndex = currentPlaylist.length - 1;
    }
  }
  
  const prevSong = currentPlaylist[prevIndex];
  if (prevSong) {
    playSong(prevSong.id);
  }
}

function togglePlayPause() {
  if (currentPlayingIndex === -1 && currentPlaylist.length > 0) {
    playSong(currentPlaylist[0].id);
    return;
  }
  
  const songRow = document.querySelector('.song-item.playing');
  if (songRow) {
    isManualStop = true;
    stopAllPlaybacks();
  } else {
    let idx = currentPlayingIndex !== -1 ? currentPlayingIndex : 0;
    if (currentPlaylist[idx]) {
      playSong(currentPlaylist[idx].id);
    }
  }
}

function toggleShuffle() {
  isShuffle = !isShuffle;
  const btn = document.getElementById('ctrl-shuffle');
  if (btn) {
    if (isShuffle) btn.style.color = 'var(--color-amber)';
    else btn.style.color = '';
  }
}

function toggleRepeat() {
  isRepeat = !isRepeat;
  const btn = document.getElementById('ctrl-repeat');
  if (btn) {
    if (isRepeat) btn.style.color = 'var(--color-amber)';
    else btn.style.color = '';
  }
}

// 8.5 Custom Song File (.123) Upload & Playback
function uploadAndPlaySong(inputElement) {
  const file = inputElement.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = async function(event) {
    const text = event.target.result;
    
    // Stop any active song playing in client
    stopAllPlaybacks();
    
    try {
      const response = await fetch(`${settings.hostApi}/api/arduino/play_song_file`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ file_content: text })
      });
      
      if (response.ok) {
        console.log("[PLAYER] Memulai pemutaran file .123 di server.");
        startPlaybackStatusPolling();
      } else {
        alert("Gagal memutar file lagu di server.");
      }
    } catch (e) {
      alert("Gagal terhubung ke API server.");
    }
  };
  
  reader.readAsText(file);
}

function stopSongFile() {
  // Send stop request to python backend
  fetch(`${settings.hostApi}/api/arduino/stop_song`)
    .then(() => {
      console.log("[PLAYER] Menghentikan pemutaran lagu di server.");
    })
    .catch(() => {});
}

// 9. Repeater Section (Pitch Tuning via Websocket)
async function toggleRepeaterListening() {
  const micBtn = document.getElementById('mic-repeater-btn');
  const sonar = document.getElementById('repeater-sonar');
  const statusText = document.getElementById('repeater-status');

  if (repeaterState === 'playing') {
    // Stop playback if playing
    stopAllPlaybacks();
    return;
  }

  if (repeaterState === 'recording') {
    repeaterState = 'idle';
    if (micBtn) micBtn.classList.remove('active');
    if (sonar) sonar.classList.remove('active');
    if (statusText) statusText.textContent = 'Menjalankan analisis presisi vokal...';
    
    if (currentRecDuration >= 100 && currentRecNote !== null) {
      recordedSequence.push({ note: typeof finalizeSegmentNote === 'function' ? finalizeSegmentNote(segNoteFrames) : currentRecNote, duration: Math.round(currentRecDuration) });
    }
    currentRecNote = null;
    currentRecDuration = 0;
    segNoteFrames = [];

    const keySig = getBackendKeySig();
    
    const executeBackendPreprocess = async () => {
      try {
        const response = await fetch(`${settings.hostApi}/api/repeater/preprocess_latest`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ key_sig: keySig })
        });
        const result = await response.json();
        if (result.status === 'success' && result.sequence && result.sequence.length > 0) {
          recordedSequence = result.sequence;
          if (statusText) statusText.textContent = `Analisis Presisi Selesai: terdeteksi ${recordedSequence.length} nada!`;
          playRepeaterSequence(recordedSequence, statusText, micBtn, sonar);
        } else {
          playRepeaterSequence(recordedSequence, statusText, micBtn, sonar);
        }
      } catch (e) {
        console.error("Gagal menjalankan preprocessing:", e);
        playRepeaterSequence(recordedSequence, statusText, micBtn, sonar);
      }
    };

    if (repeaterSocket && repeaterSocket.readyState === WebSocket.OPEN) {
      let handshaked = false;
      repeaterSocket.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.status === 'saved' && !handshaked) {
            handshaked = true;
            if (repeaterSocket) {
              repeaterSocket.close();
              repeaterSocket = null;
            }
            executeBackendPreprocess();
          }
        } catch(e) {}
      };
      // Send explicit stop action to backend
      repeaterSocket.send(JSON.stringify({ action: "stop" }));
      
      // Safety fallback
      setTimeout(() => {
        if (!handshaked) {
          handshaked = true;
          if (repeaterSocket) {
            repeaterSocket.close();
            repeaterSocket = null;
          }
          executeBackendPreprocess();
        }
      }, 800);
    } else {
      if (repeaterSocket) {
        repeaterSocket.close();
        repeaterSocket = null;
      }
      executeBackendPreprocess();
    }
    return;
  }

  // Start recording
  repeaterState = 'recording';
  recordedSequence = [];
  currentRecNote = null;
  currentRecDuration = 0;
  let noteHistory = []; // Buffer 5 frame untuk menghaluskan nada
  let segRmsHistory = []; // Riwayat RMS dalam segmen nada aktif saat ini (untuk deteksi onset/ketukan baru)
  let lastForcedSplitTime = performance.now(); // Debounce agar tidak split berkali-kali di satu decay valley
  segNoteFrames = []; // reset buffer global untuk sesi rekaman baru
  
  if (micBtn) micBtn.classList.add('active');
  if (sonar) sonar.classList.add('active');
  if (statusText) statusText.textContent = 'Merekam nada vokal... Tekan lagi untuk putar ulang!';

  // Clear sequence UI
  const sequenceContainer = document.getElementById('repeater-note-sequence');
  if (sequenceContainer) sequenceContainer.innerHTML = '';
  window.lastRepeaterNote = null;

  // Connect to FastAPI WebSocket endpoint with high-precision timestamp tracking
  const wsHost = settings.hostApi.replace('http://', 'ws://');
  let lastFrameTime = performance.now();

  try {
    const keySigParam = encodeURIComponent(getBackendKeySig());
    repeaterSocket = new WebSocket(`${wsHost}/ws/pitch?key=${keySigParam}`);
    
    let freqHistoryBuffer = [];

    repeaterSocket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      if (repeaterState !== 'recording') return;
      
      const now = performance.now();
      const measuredDurMs = Math.max(10, now - lastFrameTime);
      lastFrameTime = now;

      const confidence = data.confidence !== undefined ? data.confidence : 1.0;
      const centsDev = data.cents_dev || 0.0;
      
      // Confidence-Based Gating (0.15 threshold for soft human singing voice)
      let rawNote = (data.frequency > 0 && data.note && confidence >= 0.15) ? data.note : null;
      
      noteHistory.push(rawNote);
      if (noteHistory.length > 4) noteHistory.shift();
      
      // Hysteresis Pitch Stabilizer: Require 3-frame majority agreement to switch note
      // (eliminates jittery pitch wobbles) — but ONLY when switching between two
      // established notes. When starting FROM SILENCE (currentRecNote === null),
      // there's no "previous note" to protect against, so waiting for a 3-frame
      // majority just eats the first ~150-200ms of the very first syllable/beat.
      // Fast-lock instead: accept the raw note as soon as it's confidently voiced.
      let activeNote = currentRecNote;
      if (currentRecNote === null) {
        if (rawNote !== null && confidence >= 0.20) {
          activeNote = rawNote;
        }
      } else if (noteHistory.length >= 3) {
        const counts = {};
        for (let n of noteHistory) {
          const key = n === null ? 'NULL_NOTE' : n;
          counts[key] = (counts[key] || 0) + 1;
        }
        let maxCount = 0;
        let majNote = rawNote;
        for (let key in counts) {
          if (counts[key] > maxCount) {
            maxCount = counts[key];
            majNote = key === 'NULL_NOTE' ? null : key;
          }
        }
        if (maxCount >= 3) {
          activeNote = majNote;
        }
      }
      
      // Envelope-based Onset Detector for the live mic path (mirrors the backend's
      // decay-valley logic in segment_vocal_melody_frames). Without this, the live
      // path only starts a new note when the PITCH changes — so the same note sung
      // twice/thrice as separate beats ("nada sama, ketukan beda") would incorrectly
      // collapse into one long held note. Here we also force a new beat boundary when
      // the loudness dips into a valley then the singer re-attacks, even if the pitch
      // (activeNote) stayed exactly the same.
      const rms = data.rms || 0.0;
      segRmsHistory.push(rms);
      if (segRmsHistory.length > 40) segRmsHistory.shift(); // cap buffer growth

      const maxSegRms = Math.max(...segRmsHistory, 0.0001);
      const decayValley = activeNote === currentRecNote &&
        activeNote !== null &&
        rms <= 0.35 * maxSegRms &&
        segRmsHistory.length >= 3 &&
        currentRecDuration >= 120 &&
        (now - lastForcedSplitTime) >= 150; // debounce: avoid multiple splits per valley

      // Accumulate exact measured duration per note/silence segment using high-res performance timestamps
      if (activeNote === currentRecNote && !decayValley) {
        currentRecDuration += measuredDurMs;
        segNoteFrames.push({ note: activeNote, rms: rms });
      } else {
        if (currentRecDuration > 0) {
          recordedSequence.push({ 
            note: finalizeSegmentNote(segNoteFrames), 
            duration: Math.round(currentRecDuration),
            centsDev: centsDev,
            confidence: confidence 
          });
        }
        currentRecNote = activeNote;
        currentRecDuration = measuredDurMs;
        segRmsHistory = [rms];
        segNoteFrames = [{ note: activeNote, rms: rms }];
        if (decayValley) lastForcedSplitTime = now;
      }

      // HUD & Debug Overlay Updates
      const confEl = document.getElementById('repeater-conf');
      const centsEl = document.getElementById('repeater-cents');
      if (confEl) confEl.textContent = `${Math.round(confidence * 100)}%`;
      if (centsEl) centsEl.textContent = `${centsDev >= 0 ? '+' : ''}${centsDev.toFixed(1)} c`;

      if (data.frequency > 0 && confidence >= 0.15) {
        document.getElementById('repeater-freq').textContent = `${data.frequency.toFixed(1)} Hz`;
        document.getElementById('repeater-note').textContent = activeNote || '---';
        if (activeNote) {
          const hw = mapPitchNameToNoteNumber(activeNote);
          if (hw) {
            highlightKeyProgrammatic(hw.note, hw.angklung, false);
          }
        }
      } else {
        document.getElementById('repeater-note').textContent = '---';
      }
    };

    repeaterSocket.onclose = () => {
      console.log("[WS-Repeater] WebSocket terputus.");
    };
    repeaterSocket.onerror = (err) => {
      console.warn("[WS-Repeater] WebSocket error, melanjutkan perekaman:", err);
    };
  } catch (e) {
    console.error("Gagal membuka WebSocket Repeater:", e);
  }
}

// Legato Vocal Melodic Cleaner (Eliminates Morse-code choppy clicks & produces smooth sustained song melody)
function cleanRepeaterSequence(rawSequence) {
  if (!rawSequence || rawSequence.length === 0) return [];

  // Step 1: Merge identical adjacent notes OR 1-frame micro-jitter (< 65ms).
  let merged = [];
  for (let item of rawSequence) {
    let note = item.note || null;
    let dur = item.duration || item.duration_ms || 200;

    if (merged.length > 0) {
      const last = merged[merged.length - 1];

      // Merge identical notes only if one of the fragments is micro-jitter < 90ms
      if (last.note === note) {
        if (dur < 90 || last.duration < 90) {
          last.duration += dur;
          continue;
        }
      }

      // Micro-transition wobble absorber: absorb 1-frame noise glitches (< 65ms)
      if (last.note !== null && note !== null) {
        const p1 = parsePitchNote(last.note);
        const p2 = parsePitchNote(note);
        if (p1 && p2 && dur < 65) {
          last.duration += dur;
          continue;
        }
      }
    }
    merged.push({ note: note, duration: dur });
  }

  // Step 2: Micro-Gap Legato Filler (Connect tiny silence gaps < 180ms between notes)
  let legato = [];
  for (let i = 0; i < merged.length; i++) {
    const item = merged[i];
    const prev = i > 0 ? merged[i - 1] : null;
    const next = i < merged.length - 1 ? merged[i + 1] : null;

    if (item.note === null && item.duration < 180 && prev && next && prev.note !== null && next.note !== null) {
      prev.duration += item.duration;
      continue;
    }
    legato.push(item);
  }

  // Step 3: Standalone Transient Noise Glitch Filter (< 70ms)
  let filtered = [];
  for (let i = 0; i < legato.length; i++) {
    const item = legato[i];
    const prev = i > 0 ? legato[i - 1] : null;
    const next = i < legato.length - 1 ? legato[i + 1] : null;

    if (item.note === null) {
      filtered.push(item);
      continue;
    }

    // Filter out short transient noise clicks/glitches under 70ms
    if (item.duration < 70) {
      if (prev && prev.note !== null) {
        prev.duration += item.duration;
      } else if (next && next.note !== null) {
        next.duration += item.duration;
      }
    } else {
      filtered.push(item);
    }
  }

  // Step 4: Octave-Spike Stabilizer (Fix isolated 1-octave jumps: C4 -> C5 -> C4)
  for (let i = 1; i < filtered.length - 1; i++) {
    const prev = filtered[i - 1];
    const curr = filtered[i];
    const next = filtered[i + 1];

    if (prev.note && curr.note && next.note) {
      const pPrev = parsePitchNote(prev.note);
      const pCurr = parsePitchNote(curr.note);
      const pNext = parsePitchNote(next.note);

      if (pPrev && pCurr && pNext) {
        const diffPrev = Math.abs(pCurr.midi - pPrev.midi);
        const diffNext = Math.abs(pCurr.midi - pNext.midi);

        // If current note is an isolated 1-octave or 2-octave spike between prev and next
        if ((diffPrev === 12 || diffPrev === 24) && (diffNext === 12 || diffNext === 24)) {
          let adjustedMidi = pCurr.midi;
          if (pCurr.midi > pPrev.midi) adjustedMidi -= 12;
          else adjustedMidi += 12;
          curr.note = midiToPitchNameString(adjustedMidi, pCurr.isBass);
        }
      }
    }
  }

  // Step 5: Trim leading and trailing silence
  while (filtered.length > 0 && filtered[0].note === null) {
    filtered.shift();
  }
  while (filtered.length > 0 && filtered[filtered.length - 1].note === null) {
    filtered.pop();
  }

  return filtered;
}

// =========================================================================
// Musical Auto-Tune & Harmonic Pitch Quantizer Engine
// Automatically corrects off-key ("fals") vocal notes to nearest scale degree
// =========================================================================
const PITCH_CLASS_NAMES = ['c', 'c#', 'd', 'd#', 'e', 'f', 'f#', 'g', 'g#', 'a', 'a#', 'b'];

const HARMONIC_SCALES = {
  "c_major":     [0, 2, 4, 5, 7, 9, 11], // C, D, E, F, G, A, B
  "g_major":     [7, 9, 11, 0, 2, 4, 6], // G, A, B, C, D, E, F#
  "f_major":     [5, 7, 9, 10, 0, 2, 4], // F, G, A, A#, C, D, E
  "d_major":     [2, 4, 6, 7, 9, 11, 1], // D, E, F#, G, A, B, C#
  "bb_major":    [10, 0, 2, 3, 5, 7, 9], // Bb, C, D, Eb, F, G, A
  "a_minor":     [9, 11, 0, 2, 4, 5, 7], // A, B, C, D, E, F, G
  "e_minor":     [4, 6, 7, 9, 11, 0, 2], // E, F#, G, A, B, C, D
  "pelog_sunda": [0, 1, 5, 7, 8],        // C, C#, F, G, G#
  "slendro":     [0, 2, 5, 7, 9]         // C, D, F, G, A
};

// Pitch-class of "Do" (tonic) for each scale, used for correct Do-Re-Mi labeling.
// Relative minors share the same Do as their major counterpart (Am -> Do=C, Em -> Do=G),
// consistent with Indonesian numbered-notation convention and with the backend's KEY_ROOTS_MIDI.
const SCALE_ROOT_PITCH_CLASS = {
  "c_major": 0, "a_minor": 0,
  "g_major": 7, "e_minor": 7,
  "f_major": 5,
  "d_major": 2,
  "bb_major": 10,
  "pelog_sunda": 0,
  "slendro": 0
};

// Maps the dropdown's scale key to the backend's key_sig string (see KEY_ROOTS_MIDI in api.py)
const SCALE_KEY_TO_BACKEND_KEYSIG = {
  "c_major": "C", "g_major": "G", "f_major": "F",
  "d_major": "D", "bb_major": "Bb", "a_minor": "Am"
};

// Currently selected key from the "Tangga Nada (Key)" dropdown. 'auto' = auto-detect.
let selectedRepeaterKey = 'c_major';

// Resolve which scale to snap/label against: explicit user selection wins over auto-detect.
function resolveTargetScaleKey(sequence) {
  if (selectedRepeaterKey && selectedRepeaterKey !== 'auto' && HARMONIC_SCALES[selectedRepeaterKey]) {
    return selectedRepeaterKey;
  }
  // Fallback: auto-detect dominant scale from the recorded sequence itself.
  let pcWeights = new Array(12).fill(0);
  for (let item of sequence) {
    if (!item.note) continue;
    const parsed = parsePitchNote(item.note);
    if (parsed) pcWeights[parsed.pitchClass] += item.duration;
  }
  let bestScaleKey = "c_major";
  let maxScore = -1;
  for (let scaleKey in HARMONIC_SCALES) {
    let score = 0;
    for (let pc of HARMONIC_SCALES[scaleKey]) score += pcWeights[pc];
    if (score > maxScore) {
      maxScore = score;
      bestScaleKey = scaleKey;
    }
  }
  return bestScaleKey;
}

// Returns the backend key_sig string ("C", "G", "Bb", "Am", ...) for the currently
// resolved key, so the server-side solfege labeling matches what's shown/played client-side.
function getBackendKeySig(sequence) {
  const scaleKey = resolveTargetScaleKey(sequence || []);
  return SCALE_KEY_TO_BACKEND_KEYSIG[scaleKey] || "C";
}

// Computes the Do-Re-Mi solfege label for a pitch-class RELATIVE to the active key's root,
// instead of assuming Do=C always.
function pitchClassToScaleDegreeLabel(pitchClass, rootPitchClass) {
  if (pitchClass === undefined || pitchClass === null) return '';
  const doremiLabels = ['1 (Do)', '1/ (Do#)', '2 (Re)', '2/ (Re#)', '3 (Mi)', '4 (Fa)', '4/ (Fa#)', '5 (Sol)', '5/ (Sol#)', '6 (La)', '6/ (La#)', '7 (Si)'];
  const semitoneFromRoot = ((pitchClass - rootPitchClass) % 12 + 12) % 12;
  return doremiLabels[semitoneFromRoot];
}

function parsePitchNote(noteStr) {
  if (!noteStr) return null;
  const clean = noteStr.toLowerCase().replace('_bass', '');
  const match = clean.match(/^([a-g]#?)(\d+)$/);
  if (!match) return null;
  const name = match[1];
  const octave = parseInt(match[2], 10);
  const pc = PITCH_CLASS_NAMES.indexOf(name);
  if (pc === -1) return null;
  const midi = (octave + 1) * 12 + pc;
  return { name, octave, pitchClass: pc, midi, isBass: noteStr.toLowerCase().includes('_bass') };
}

function midiToPitchNameString(midi, isBass = false) {
  const pc = midi % 12;
  const octave = Math.floor(midi / 12) - 1;
  let name = PITCH_CLASS_NAMES[pc].toUpperCase();
  let result = `${name}${octave}`;
  if (isBass && (result === "F4" || result === "F#4" || result === "G4")) {
    result += "_bass";
  }
  return result;
}

function detectBestHarmonicScale(sequence) {
  let pcWeights = new Array(12).fill(0);
  for (let item of sequence) {
    if (!item.note) continue;
    const parsed = parsePitchNote(item.note);
    if (parsed) {
      pcWeights[parsed.pitchClass] += item.duration;
    }
  }

  let bestScaleKey = "c_major";
  let maxScore = -1;

  for (let scaleKey in HARMONIC_SCALES) {
    const scaleClasses = HARMONIC_SCALES[scaleKey];
    let score = 0;
    for (let pc of scaleClasses) {
      score += pcWeights[pc];
    }
    if (score > maxScore) {
      maxScore = score;
      bestScaleKey = scaleKey;
    }
  }

  return HARMONIC_SCALES[bestScaleKey] || HARMONIC_SCALES["c_major"];
}

function snapNoteToHarmonicScale(noteStr, scaleClasses) {
  if (!noteStr) return null;
  const parsed = parsePitchNote(noteStr);
  if (!parsed) return noteStr;

  if (scaleClasses.includes(parsed.pitchClass)) {
    return noteStr; // Already in scale -> keep exact note!
  }

  // Cari nada terdekat di tangga nada. Jangkauan diperlebar ke ±6 semitone (bukan ±2)
  // supaya SELALU ketemu kandidat walau: (a) penyanyi meleset jauh dari nada yang
  // dimaksud ("fals"/kurang presisi pitch), atau (b) tangga nada pentatonis (Pelog/
  // Slendro) yang celah antar nadanya bisa sampai 4 semitone -- dengan ±2 saja,
  // nada yang meleset di luar celah itu tidak akan ter-snap sama sekali dan lolos
  // sebagai nada asli yang salah.
  let bestMidi = parsed.midi;
  let minDiff = 999;

  for (let delta = -6; delta <= 6; delta++) {
    if (delta === 0) continue;
    const candidateMidi = parsed.midi + delta;
    const candidatePc = (candidateMidi % 12 + 12) % 12;
    if (scaleClasses.includes(candidatePc)) {
      const diff = Math.abs(delta);
      if (diff < minDiff) {
        minDiff = diff;
        bestMidi = candidateMidi;
      }
    }
  }

  // Fold into the physical Angklung MIDI range BY OCTAVE (preserve pitch-class),
  // instead of hard-clamping which corrupts the note identity for anything
  // outside E3-C7 (same bug class fixed on the backend in pitch_hz_to_scale_degree).
  while (bestMidi < 52) bestMidi += 12;
  while (bestMidi > 96) bestMidi -= 12;
  return midiToPitchNameString(bestMidi, parsed.isBass);
}

function autoTuneHarmonicSequence(sequence) {
  if (!sequence || sequence.length === 0) return [];

  const targetScaleKey = resolveTargetScaleKey(sequence);
  const targetScale = HARMONIC_SCALES[targetScaleKey];
  const rootPc = SCALE_ROOT_PITCH_CLASS[targetScaleKey] ?? 0;

  let tunedSequence = sequence.map(item => {
    if (!item.note) return { note: null, duration: item.duration };
    const tunedNote = snapNoteToHarmonicScale(item.note, targetScale);
    const scaleDegree = pitchClassToScaleDegreeLabel(parsePitchNote(tunedNote)?.pitchClass, rootPc);
    return { note: tunedNote, duration: item.duration, scaleDegree };
  });

  // Kept separate on purpose — see note in applyTieredAutotune above about preserving
  // "nada sama, ketukan beda".
  return tunedSequence;
}

// 100% Diatonic Scale Auto-Tune & Harmonics Quantizer (Eliminates all fals chromatic accidentals)
function applyTieredAutotune(sequence, mode = 'soft') {
  if (!sequence || sequence.length === 0) return sequence;

  // Resolve dominant harmonic key scale: respects explicit user Key selection first,
  // falls back to auto-detection from the recorded melody only when Key = "Auto-Detect".
  const targetScaleKey = resolveTargetScaleKey(sequence);
  const targetScale = HARMONIC_SCALES[targetScaleKey];
  const rootPc = SCALE_ROOT_PITCH_CLASS[targetScaleKey] ?? 0;

  let tunedSequence = sequence.map(item => {
    if (!item.note) return item;
    const parsed = parsePitchNote(item.note);
    if (!parsed) return item;

    // 100% Quantize note to clean harmonic scale to eliminate all off-key ("fals") accidentals
    const snapped = snapNoteToHarmonicScale(item.note, targetScale);
    const snappedParsed = parsePitchNote(snapped);
    return { 
      note: snapped, 
      duration: item.duration, 
      centsDev: item.centsDev,
      // Recompute the solfege label relative to the ACTIVE key's root, not a fixed
      // Do=C table, so the badge matches whichever Key is actually selected/detected.
      scaleDegree: pitchClassToScaleDegreeLabel(snappedParsed?.pitchClass, rootPc)
    };
  });

  // NOTE: We intentionally do NOT re-merge adjacent notes that snapped to the same
  // pitch anymore. Two adjacent segments reaching here already represent distinct
  // beats/syllables from the onset segmenter (backend) or the live decay-valley
  // detector (mic), even when they happen to be the same note ("nada sama, ketukan
  // beda") — merging them here would erase exactly that rhythmic distinction.
  return tunedSequence;
}

function applySmartAutoTune(sequence) {
  return applyTieredAutotune(sequence, repeaterProcessingMode);
}

// Convert recorded pitch sequence to official .123 V1 Lead Vocal Melody notation string
function convertSequenceTo123V1(sequence) {
  if (!sequence || sequence.length === 0) return "V1: | 0 |";

  const doremiMap = { 'c': '1', 'c#': '1/', 'd': '2', 'd#': '2/', 'e': '3', 'f': '4', 'f#': '4/', 'g': '5', 'g#': '5/', 'a': '6', 'a#': '6/', 'b': '7' };

  let tokens = [];
  for (let item of sequence) {
    if (!item.note) {
      tokens.push("0");
      continue;
    }

    const parsed = parsePitchNote(item.note);
    if (!parsed) continue;

    let baseNum = doremiMap[parsed.name] || '1';
    let formattedNum = baseNum;

    if (parsed.octave <= 3) {
      formattedNum += ',';
    } else if (parsed.octave >= 5) {
      formattedNum += "'".repeat(parsed.octave - 4);
    }

    const beats = Math.round(item.duration / 350);
    if (beats <= 1) {
      if (item.duration <= 220) {
        formattedNum += '-';
      }
      tokens.push(formattedNum);
    } else {
      tokens.push(formattedNum);
      for (let b = 1; b < Math.min(6, beats); b++) {
        tokens.push(".");
      }
    }
  }

  let v1Bars = [];
  let currentMeasure = [];

  for (let tok of tokens) {
    currentMeasure.push(tok);
    if (currentMeasure.length >= 4) {
      v1Bars.push(currentMeasure.join(" "));
      currentMeasure = [];
    }
  }
  if (currentMeasure.length > 0) {
    v1Bars.push(currentMeasure.join(" "));
  }

  return `V1: | ${v1Bars.join(" | ")} |`;
}

// Render transcribed vocal melody as clean, minimal, professional Note Cards (No emojis, soft natural themes)
function renderRepeaterNoteChips(sequence, activeIndex = -1) {
  const sequenceContainer = document.getElementById('repeater-note-sequence');
  if (!sequenceContainer) return;
  sequenceContainer.innerHTML = '';

  const chipsWrapper = document.createElement('div');
  chipsWrapper.style.display = 'flex';
  chipsWrapper.style.flexWrap = 'wrap';
  chipsWrapper.style.gap = '10px';
  chipsWrapper.style.width = '100%';
  chipsWrapper.style.justifyContent = 'center';
  chipsWrapper.style.padding = '6px 2px';

  // Clean, natural pastel color themes for scale degrees (No heavy gradients, no emojis)
  const DEGREE_THEMES = [
    { bg: '#ECFDF5', text: '#047857', border: '#A7F3D0' }, // 1 Do (Soft Emerald)
    { bg: '#EFF6FF', text: '#1D4ED8', border: '#BFDBFE' }, // 2 Re (Soft Blue)
    { bg: '#F5F3FF', text: '#6D28D9', border: '#DDD6FE' }, // 3 Mi (Soft Purple)
    { bg: '#FFF7ED', text: '#C2410C', border: '#FFEDD5' }, // 4 Fa (Soft Orange)
    { bg: '#FFFBEB', text: '#B45309', border: '#FDE68A' }, // 5 Sol (Soft Amber)
    { bg: '#FDF2F8', text: '#BE185D', border: '#FBCFE8' }, // 6 La (Soft Pink)
    { bg: '#ECFEFF', text: '#0E7490', border: '#CFFAFE' }  // 7 Si (Soft Cyan)
  ];

  sequence.forEach((item, idx) => {
    if (!item.note) return;

    const parsed = parsePitchNote(item.note);
    const pc = parsed ? parsed.pitchClass : 0;
    const theme = DEGREE_THEMES[pc % 7];

    let scaleBadge = item.scaleDegree || '';
    if (!scaleBadge && parsed) {
      const rootPc = SCALE_ROOT_PITCH_CLASS[selectedRepeaterKey] ?? 0;
      scaleBadge = pitchClassToScaleDegreeLabel(parsed.pitchClass, rootPc);
    }

    const card = document.createElement('div');
    card.className = 'repeater-note-card' + (idx === activeIndex ? ' active-playing' : '');
    card.style.background = idx === activeIndex ? '#ECFDF5' : '#FFFFFF';
    card.style.border = idx === activeIndex ? '2px solid #10B981' : '1px solid #E5E7EB';
    card.style.borderRadius = '14px';
    card.style.padding = '10px 14px';
    card.style.display = 'flex';
    card.style.flexDirection = 'column';
    card.style.alignItems = 'center';
    card.style.gap = '5px';
    card.style.minWidth = '88px';
    card.style.boxShadow = idx === activeIndex ? '0 6px 18px rgba(16, 185, 129, 0.25)' : '0 2px 8px rgba(0,0,0,0.04)';
    card.style.cursor = 'pointer';
    card.style.userSelect = 'none';
    card.style.transition = 'all 0.2s ease';
    card.title = 'Klik untuk memperdengarkan nada ini';

    if (idx === activeIndex) {
      card.style.transform = 'translateY(-3px)';
    }

    // Top Scale Degree Badge (Clean pastel pill, zero emojis)
    const badge = document.createElement('div');
    badge.style.background = theme.bg;
    badge.style.color = theme.text;
    badge.style.border = `1px solid ${theme.border}`;
    badge.style.fontWeight = '700';
    badge.style.fontSize = '12px';
    badge.style.padding = '2px 10px';
    badge.style.borderRadius = '10px';
    badge.textContent = scaleBadge || item.note;

    // Middle Pitch Name
    const pitchText = document.createElement('div');
    pitchText.style.fontWeight = '800';
    pitchText.style.fontSize = '18px';
    pitchText.style.color = '#111827';
    pitchText.textContent = item.note;

    // Bottom Duration Pill (Clean text, no emoji)
    const durPill = document.createElement('div');
    durPill.style.fontSize = '11px';
    durPill.style.fontWeight = '600';
    durPill.style.color = '#6B7280';
    durPill.style.background = '#F9FAFB';
    durPill.style.padding = '2px 8px';
    durPill.style.borderRadius = '6px';
    durPill.textContent = `${(item.duration / 1000).toFixed(1)}s`;

    card.appendChild(badge);
    card.appendChild(pitchText);
    card.appendChild(durPill);

    card.addEventListener('mouseenter', () => {
      if (idx !== activeIndex) {
        card.style.transform = 'translateY(-3px)';
        card.style.boxShadow = '0 6px 16px rgba(0,0,0,0.08)';
        card.style.borderColor = '#D1D5DB';
      }
    });

    card.addEventListener('mouseleave', () => {
      if (idx !== activeIndex) {
        card.style.transform = 'translateY(0)';
        card.style.boxShadow = '0 2px 8px rgba(0,0,0,0.04)';
        card.style.borderColor = '#E5E7EB';
      }
    });

    card.addEventListener('click', () => {
      let hw = mapPitchNameToNoteNumber(item.note);
      if (hw) {
        document.getElementById('repeater-note').textContent = item.note;
        highlightKeyProgrammatic(hw.note, hw.angklung, true, item.duration);
        playChordForNoteNumSustained(hw.note, hw.angklung, item.duration);
      }
    });

    chipsWrapper.appendChild(card);
  });

  sequenceContainer.appendChild(chipsWrapper);
}

// Handle Vocal File Upload (.WAV / .MP3) for Repeater
async function handleVocalFileUpload(input) {
  if (!input || !input.files || input.files.length === 0) return;
  const file = input.files[0];
  
  const statusText = document.getElementById('repeater-status');
  const micBtn = document.getElementById('repeater-mic-btn');
  const sonar = document.getElementById('repeater-sonar');

  if (statusText) statusText.textContent = `Mengunggah & menganalisis melodi ${file.name}...`;
  
  const formData = new FormData();
  formData.append('file', file);
  formData.append('key_sig', getBackendKeySig());
  
  try {
    const response = await fetch(`${settings.hostApi}/api/repeater/transcribe_vocal`, {
      method: 'POST',
      body: formData
    });
    
    const result = await response.json();
    if (result.status === 'success' && result.sequence) {
      recordedSequence = result.sequence;
      if (statusText) statusText.textContent = `Selesai mentranskripsi ${result.sequence.length} not melodi dari ${file.name}!`;
      playRepeaterSequence(recordedSequence, statusText, micBtn, sonar);
    } else {
      if (statusText) statusText.textContent = `Gagal mentranskripsi file vokal: ${result.detail || 'Format tidak didukung'}`;
    }
  } catch (e) {
    console.error("Gagal mengunggah file vokal:", e);
    if (statusText) statusText.textContent = `Error mengunggah file audio vokal.`;
  }
}

// Handle Key Signature Dropdown Selection
function onRepeaterKeyChanged(selectedKey) {
  selectedRepeaterKey = selectedKey;

  const statusText = document.getElementById('repeater-status');
  const micBtn = document.getElementById('repeater-mic-btn');
  const sonar = document.getElementById('repeater-sonar');
  
  if (recordedSequence && recordedSequence.length > 0) {
    playRepeaterSequence(recordedSequence, statusText, micBtn, sonar);
  }
}

// Playback Repeater Sequence (3-Mode Tiered Playback: Off, Soft, Hard)
async function playRepeaterSequence(rawSequence, statusText, micBtn, sonar) {
  const cleaned = cleanRepeaterSequence(rawSequence);
  const sequence = applyTieredAutotune(cleaned, repeaterProcessingMode);

  renderRepeaterNoteChips(sequence, -1);

  if (sequence.length === 0) {
    if (statusText) statusText.textContent = 'Tidak ada nada stabil yang terdeteksi. Coba nyanyikan nada lebih jelas!';
    repeaterState = 'idle';
    if (micBtn) {
      micBtn.classList.remove('active');
      micBtn.classList.remove('mic-playing');
    }
    if (sonar) sonar.classList.remove('active');
    return;
  }

  repeaterState = 'playing';
  if (micBtn) {
    micBtn.classList.add('active');
    micBtn.classList.add('mic-playing');
  }
  if (sonar) sonar.classList.add('active');
  if (statusText) statusText.textContent = 'Memainkan melodi persis hasil rekaman vokal...';
  
  for (let i = 0; i < sequence.length; i++) {
    let item = sequence[i];
    let nextItem = i < sequence.length - 1 ? sequence[i + 1] : null;
    if (repeaterState !== 'playing') break;
    
    // Highlight active playing card in real time
    renderRepeaterNoteChips(sequence, i);

    if (item.note !== null) {
      let hw = mapPitchNameToNoteNumber(item.note);
      if (hw) {
        const noteElem = document.getElementById('repeater-note');
        if (noteElem) noteElem.textContent = item.note;
        
        // Highlight visual key and sustain Web Audio synth for EXACT item.duration
        highlightKeyProgrammatic(hw.note, hw.angklung, true, item.duration);
        
        // Sustain physical hardware angklung shaking throughout item.duration
        playChordForNoteNumSustained(hw.note, hw.angklung, item.duration);
        
        if (nextItem && nextItem.note === item.note && item.duration > 80) {
          await new Promise(r => setTimeout(r, Math.max(10, item.duration - 25)));
          if (noteElem) noteElem.textContent = '---';
          await new Promise(r => setTimeout(r, 25));
        } else {
          await new Promise(r => setTimeout(r, item.duration));
        }
      } else {
        await new Promise(r => setTimeout(r, item.duration));
      }
    } else {
      const noteElem = document.getElementById('repeater-note');
      if (noteElem) noteElem.textContent = '---';
      await new Promise(r => setTimeout(r, item.duration));
    }
  }
  
  renderRepeaterNoteChips(sequence, -1);

  if (repeaterState === 'playing') {
    repeaterState = 'idle';
    if (micBtn) {
      micBtn.classList.remove('active');
      micBtn.classList.remove('mic-playing');
    }
    if (sonar) sonar.classList.remove('active');
    if (statusText) statusText.textContent = 'Ketuk mikrofon untuk merekam nada';
  }
}

function playChordForNoteNumSustained(note, angklung, durationMs) {
  playChordForNoteNum(note, angklung);
  if (durationMs > 160) {
    const pulseInterval = setInterval(() => {
      playChordForNoteNum(note, angklung);
    }, 130);
    setTimeout(() => {
      clearInterval(pulseInterval);
    }, durationMs - 20);
  }
}

// Helper to trigger a single note (used by repeater tremolo)
function playChordForNoteNum(note, angklung) {
  if (angklung === 1) {
    fetch(`${settings.hostApi}/api/arduino/play_multi?a1=${note}&a3=`).catch(() => {});
  } else if (angklung === 2) {
    fetch(`${settings.hostApi}/api/arduino/play_multi?a1=${note + 16}&a3=`).catch(() => {});
  } else if (angklung === 3) {
    fetch(`${settings.hostApi}/api/arduino/play_multi?a1=&a3=${note}`).catch(() => {});
  }
}

// Maps incoming WebSocket pitch names back to 1-16 note keys
function mapPitchNameToNoteNumber(pitchName) {
  if (!pitchName) return null;
  const normalized = pitchName.toLowerCase();
  
  // Try direct match first
  if (PITCH_TO_HARDWARE[normalized]) {
    return PITCH_TO_HARDWARE[normalized];
  }
  
  // Try bass fallback for Angklung 3 upper notes
  if (PITCH_TO_HARDWARE[normalized + "_bass"]) {
    return PITCH_TO_HARDWARE[normalized + "_bass"];
  }

  // Fallback: Octave Folding for low notes (like C3, D3, Eb3) or high notes outside physical range
  const parsed = parsePitchNote(pitchName);
  if (parsed) {
    let foldedMidi = parsed.midi;
    while (foldedMidi < 52) foldedMidi += 12;
    while (foldedMidi > 96) foldedMidi -= 12;
    const foldedName = midiToPitchName(foldedMidi, parsed.isBass);
    if (PITCH_TO_HARDWARE[foldedName]) {
      return PITCH_TO_HARDWARE[foldedName];
    }
    if (PITCH_TO_HARDWARE[foldedName + "_bass"]) {
      return PITCH_TO_HARDWARE[foldedName + "_bass"];
    }
  }
  
  return null;
}

// 10. Language Classification (AI Perekam CRNN)
async function triggerLanguageClassification() {
  // Hentikan pemutaran lagu jika ada media player yang sedang berjalan
  if (typeof stopSongPlayback === 'function') {
    stopSongPlayback();
  }

  const micBtn = document.getElementById('mic-bahasa-btn');
  const sonar = document.getElementById('ai-waves');
  const statusText = document.getElementById('ai-status');

  micBtn.disabled = true;
  micBtn.classList.add('active');
  sonar.classList.add('active');
  statusText.textContent = 'Merekam kata sapaan selama 2.0 detik...';

  try {
    const response = await fetch(`${settings.hostApi}/api/record-and-classify`, { method: 'POST' });
    if (response.ok) {
      const data = await response.json();
      
      // Mode 2: Tampilkan hasil deteksi & daftar lagu di panel kanan
      setBahasaMode(2, data);

      // Otomatis putar lagu daerah yang terdeteksi
      if (data.song) {
        setTimeout(() => {
          playBahasaSong(data.song);
        }, 300);
      }
    } else {
      statusText.textContent = 'Gagal memproses klasifikasi suara.';
    }
  } catch (e) {
    statusText.textContent = 'Gagal menghubungi server backend AI.';
  } finally {
    micBtn.disabled = false;
    micBtn.classList.remove('active');
    sonar.classList.remove('active');
  }
}

function playBahasaSong(songId) {
  isBahasaPlayback = true;
  playSong(songId);
}

function highlightGreetingChip(region) {
  const chips = document.querySelectorAll('.orbit-word-badge');
  chips.forEach(chip => {
    chip.classList.remove('active-orbit-word');
  });

  if (!region) return;
  const resolvedRegion = (GREETING_TO_REGION[region.toLowerCase()] || region).toUpperCase();
  const activeChip = document.getElementById(`chip-${resolvedRegion}`);
  if (activeChip) {
    activeChip.classList.add('active-orbit-word');
  }
}

function setBahasaMode(mode, data = {}) {
  const instructions = document.getElementById('bahasa-instructions');
  const songsListContainer = document.getElementById('bahasa-songs-list');
  const aiClass = document.getElementById('ai-class');
  const aiConf = document.getElementById('ai-conf');
  const aiStatus = document.getElementById('ai-status');

  if (mode === 1) {
    isBahasaPlayback = false;
    highlightGreetingChip(null);
    if (instructions) instructions.style.display = 'flex';
    if (songsListContainer) {
      songsListContainer.style.display = 'none';
      songsListContainer.innerHTML = '';
    }
    if (aiClass) aiClass.textContent = '---';
    if (aiConf) aiConf.textContent = '0%';
    if (aiStatus) aiStatus.textContent = 'Ketuk mikrofon lalu ucapkan salam daerah';
  } else if (mode === 2) {
    if (instructions) instructions.style.display = 'none';
    if (songsListContainer) songsListContainer.style.display = 'block';

    if (data.predicted_class) {
      const displayClass = data.song_title ? `${data.predicted_class.toUpperCase()} (${data.song_title})` : data.predicted_class.toUpperCase();
      if (aiClass) aiClass.textContent = displayClass;
      if (aiConf) aiConf.textContent = `${(data.confidence * 100).toFixed(0)}%`;
      if (aiStatus) aiStatus.textContent = `Deteksi kata selesai! Wilayah: ${data.region}`;
    }

    highlightGreetingChip(data.region);
    renderBahasaSongs(data.region);
  }
}

function resetBahasaPage() {
  setBahasaMode(1);
}

const GREETING_TO_REGION = {
  "adil": "KALIMANTAN",
  "horas": "BATAK",
  "kula nuwun": "JAWA",
  "peuhaba": "ACEH",
  "sampurasun": "SUNDA",
  "tabea": "SULAWESI",
  "wawawa": "PAPUA"
};

const REGION_CLUSTERS = {
  "SUNDA": { island: "Pulau Jawa", nearby: ["JAWA"] },
  "JAWA": { island: "Pulau Jawa", nearby: ["SUNDA"] },
  "ACEH": { island: "Pulau Sumatera", nearby: ["BATAK"] },
  "BATAK": { island: "Pulau Sumatera", nearby: ["ACEH"] },
  "SULAWESI": { island: "Sulawesi & Indonesia Timur", nearby: ["PAPUA", "KALIMANTAN"] },
  "PAPUA": { island: "Papua & Indonesia Timur", nearby: ["SULAWESI", "KALIMANTAN"] },
  "KALIMANTAN": { island: "Pulau Kalimantan", nearby: ["SUNDA", "SULAWESI"] }
};

function renderBahasaSongs(region) {
  const songsListContainer = document.getElementById('bahasa-songs-list');
  if (!songsListContainer || !region) return;

  const resolvedRegion = (GREETING_TO_REGION[region.toLowerCase()] || region).toUpperCase();
  const clusterInfo = REGION_CLUSTERS[resolvedRegion] || { island: "Nusantara", nearby: [] };

  // 1. Primary Songs (Exact Region)
  const primarySongs = songs.filter(s => 
    s.region.toUpperCase() === resolvedRegion || 
    s.folder.toUpperCase() === resolvedRegion
  );

  // 2. Nearby Recommended Songs (Same Island / Cluster)
  const nearbyFolders = clusterInfo.nearby.map(r => r.toUpperCase());
  const recommendedSongs = songs.filter(s => 
    nearbyFolders.includes(s.region.toUpperCase()) || 
    nearbyFolders.includes(s.folder.toUpperCase())
  );

  let htmlContent = `
    <div style="margin-bottom: 24px;">
      <span style="font-size: 13px; font-weight: 700; color: #FF8A65; letter-spacing: 2px; text-transform: uppercase; margin-bottom: 4px; display: block;">Hasil Deteksi • ${clusterInfo.island}</span>
      <h2 style="font-family: var(--font-header); font-size: 28px; font-weight: 800; color: #1A1A1A; margin: 0;">Lagu Daerah ${resolvedRegion}</h2>
    </div>
  `;

  // Primary Songs Cards
  if (primarySongs.length > 0) {
    htmlContent += primarySongs.map(song => `
      <div class="song-item" id="row-${getSongBtnId(song.id)}" onclick="playBahasaSong('${song.id}')">
        <div class="song-icon"><i class="fa-solid fa-music"></i></div>
        <div class="song-info">
          <h4>${song.title}</h4>
          <p>${song.folder} • ${song.region}</p>
        </div>
        <button class="song-play-btn" id="${getSongBtnId(song.id)}"><i class="fa-solid fa-play"></i></button>
      </div>
    `).join('');
  } else {
    htmlContent += `
      <div style="text-align: center; padding: 24px; color: #888; background: #fff; border-radius: 16px; margin-bottom: 20px; border: 1px solid #f0f2f5;">
        <p style="margin: 0;">Tidak ada lagu utama yang ditemukan untuk daerah ${resolvedRegion}.</p>
      </div>`;
  }

  // Recommended Nearby Songs Section
  if (recommendedSongs.length > 0) {
    htmlContent += `
      <div style="margin-top: 32px; margin-bottom: 16px; display: flex; align-items: center; gap: 10px;">
        <div style="background: #FFF3E0; color: #E65100; width: 34px; height: 34px; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 14px; box-shadow: 0 2px 6px rgba(230,81,0,0.15);">
          <i class="fa-solid fa-compass"></i>
        </div>
        <div>
          <h3 style="font-family: var(--font-header); font-size: 18px; font-weight: 800; color: #1A1A1A; margin: 0;">Rekomendasi Lagu Daerah Terdekat</h3>
          <span style="font-size: 12px; color: #666; font-weight: 600;">Lagu-lagu daerah tetangga di wilayah ${clusterInfo.island}</span>
        </div>
      </div>
    `;

    htmlContent += recommendedSongs.map(song => `
      <div class="song-item" id="row-${getSongBtnId(song.id)}" onclick="playBahasaSong('${song.id}')" style="background: #fafafa; border: 1px dashed #cbd5e1;">
        <div class="song-icon" style="background: #fff; color: #FF8A65; box-shadow: 0 2px 6px rgba(0,0,0,0.05);"><i class="fa-solid fa-location-dot"></i></div>
        <div class="song-info">
          <h4>${song.title}</h4>
          <p><span style="background: #FFECB3; color: #795548; padding: 2px 8px; border-radius: 6px; font-weight: 700; font-size: 10px; margin-right: 6px;">${song.folder}</span> ${song.region}</p>
        </div>
        <button class="song-play-btn" id="${getSongBtnId(song.id)}"><i class="fa-solid fa-play"></i></button>
      </div>
    `).join('');
  }

  songsListContainer.innerHTML = htmlContent;
}

// Helper: Stop all active timers/sockets when exiting a page
function stopAllPlaybacks() {
  // Stop playback status polling and hide panel
  stopPlaybackStatusPolling();
  hidePlayerPanel();

  // Stop Song Interval
  if (activeSongInterval) {
    clearInterval(activeSongInterval);
    activeSongInterval = null;
  }

  // Stop Play buttons class & remove playing from rows
  const playButtons = document.querySelectorAll('.song-play-btn');
  playButtons.forEach(btn => {
    btn.innerHTML = '<i class="fa-solid fa-play"></i>';
  });
  
  const songRows = document.querySelectorAll('.song-item');
  songRows.forEach(row => {
    row.classList.remove('playing');
  });

  const globalPlayPauseIcon = document.getElementById('icon-playpause');
  if (globalPlayPauseIcon) {
    globalPlayPauseIcon.classList.remove('fa-pause');
    globalPlayPauseIcon.classList.add('fa-play');
  }

  // Stop Repeater WebSocket
  if (repeaterSocket) {
    try { repeaterSocket.close(); } catch (_) {}
    repeaterSocket = null;
  }
  repeaterState = 'idle';
  
  const micBtn = document.getElementById('mic-repeater-btn');
  if (micBtn) {
    micBtn.classList.remove('active');
    micBtn.classList.remove('mic-playing');
  }
  
  const sonar = document.querySelector('.sonar-wave.wave-green');
  if (sonar) sonar.classList.remove('active');
  
  const repStatus = document.getElementById('repeater-status');
  if (repStatus) repStatus.textContent = 'Ketuk mikrofon untuk merekam nada';

  // Stop any custom song playing on Python backend
  fetch(`${settings.hostApi}/api/arduino/stop_song`).catch(() => {});
}

// 11. Homepage Slider Logic
let currentMenuIndex = 0;

function updateMenuSlider() {
  const track = document.getElementById('menu-slider-track');
  if (!track) return;
  const cards = track.querySelectorAll('.cn-card-wrapper');
  
  cards.forEach((card, index) => {
    if (index === currentMenuIndex) {
      card.classList.add('active-slide');
    } else {
      card.classList.remove('active-slide');
    }
  });
}

function nextMenu() {
  const track = document.getElementById('menu-slider-track');
  if (!track) return;
  const cards = track.querySelectorAll('.cn-card-wrapper');
  if (cards.length === 0) return;
  
  currentMenuIndex = (currentMenuIndex + 1) % cards.length;
  updateMenuSlider();
}

function prevMenu() {
  const track = document.getElementById('menu-slider-track');
  if (!track) return;
  const cards = track.querySelectorAll('.cn-card-wrapper');
  if (cards.length === 0) return;
  
  currentMenuIndex = (currentMenuIndex - 1 + cards.length) % cards.length;
  updateMenuSlider();
}

// Initialize on load
document.addEventListener('DOMContentLoaded', () => {
  updateMenuSlider();
});
// 12. Global Search Logic
const systemIndex = [
  { type: 'Menu', title: 'Pustaka Lagu', subtitle: 'Koleksi Daerah', icon: 'fa-music', badgeClass: 'badge-menu', action: () => navigateTo('page-pustaka') },
  { type: 'Menu', title: 'Kontrol Manual', subtitle: 'Main Interaktif', icon: 'fa-keyboard', badgeClass: 'badge-menu', action: () => navigateTo('page-manual') },
  { type: 'Menu', title: 'Repeater', subtitle: 'Ikuti Irama', icon: 'fa-microphone', badgeClass: 'badge-menu', action: () => navigateTo('page-repeater') },
  { type: 'Menu', title: 'Deteksi Bahasa', subtitle: 'Perintah Suara', icon: 'fa-volume-high', badgeClass: 'badge-menu', action: () => navigateTo('page-bahasa') },
  { type: 'Pengaturan', title: 'Koneksi Port Angklung', subtitle: 'Serial COM, Host API, Simulasi', icon: 'fa-plug', badgeClass: 'badge-pengaturan', action: () => { toggleSettingsModal(); switchSettingTab('koneksi'); } },
  { type: 'Pengaturan', title: 'Volume & Audio', subtitle: 'Volume Synth, Fisik, Keseimbangan Track', icon: 'fa-sliders', badgeClass: 'badge-pengaturan', action: () => { toggleSettingsModal(); switchSettingTab('volume'); } }
];

function handleGlobalSearch(query) {
  const dropdown = document.getElementById('global-search-dropdown');
  if (!dropdown) return;

  if (query.length < 2) {
    dropdown.classList.add('hide');
    return;
  }

  query = query.toLowerCase();
  
  // Search system index
  const matchedSystem = systemIndex.filter(item => 
    item.title.toLowerCase().includes(query) || item.subtitle.toLowerCase().includes(query) || item.type.toLowerCase().includes(query)
  );
  
  // Search songs (limit to 5 results to keep dropdown clean)
  const matchedSongs = songs.filter(s => 
    s.title.toLowerCase().includes(query) || s.folder.toLowerCase().includes(query) || s.region.toLowerCase().includes(query)
  ).slice(0, 5).map(s => ({
    type: 'Lagu',
    title: s.title,
    subtitle: `${s.folder} • ${s.region}`,
    icon: 'fa-compact-disc',
    badgeClass: 'badge-lagu',
    action: () => {
      document.getElementById('cn-search-input').value = '';
      dropdown.classList.add('hide');
      navigateTo('page-pustaka');
      setTimeout(() => playSong(s.id, s.notes), 400); // Give time for UI transition
    }
  }));

  const allResults = [...matchedSystem, ...matchedSongs];

  if (allResults.length === 0) {
    dropdown.innerHTML = `<div style="padding: 20px; text-align: center; color: #888;">Tidak ada hasil untuk "${query}"</div>`;
  } else {
    dropdown.innerHTML = allResults.map((result, idx) => `
      <div class="search-result-item" onclick="executeGlobalSearchAction(${idx})">
        <div class="search-result-icon">
          <i class="fa-solid ${result.icon}"></i>
        </div>
        <div class="search-result-info">
          <h4>${result.title}</h4>
          <p>${result.subtitle}</p>
        </div>
        <div class="search-result-badge ${result.badgeClass}">${result.type}</div>
      </div>
    `).join('');
    
    // Store results globally to execute actions via onclick string
    window.currentGlobalSearchResults = allResults;
  }
  
  dropdown.classList.remove('hide');
}

window.executeGlobalSearchAction = function(index) {
  const result = window.currentGlobalSearchResults[index];
  if (result && result.action) {
    const dropdown = document.getElementById('global-search-dropdown');
    document.getElementById('cn-search-input').value = '';
    dropdown.classList.add('hide');
    result.action();
  }
};

// Close dropdown on click outside
document.addEventListener('click', (e) => {
  const searchContainer = document.querySelector('.nav-search');
  const dropdown = document.getElementById('global-search-dropdown');
  if (searchContainer && dropdown && !searchContainer.contains(e.target)) {
    dropdown.classList.add('hide');
  }
});

// Explicitly register UI event handler functions on global window object
window.navigateTo = navigateTo;
window.toggleSettingsModal = toggleSettingsModal;
window.switchSettingTab = switchSettingTab;
window.saveConnectionSettings = saveConnectionSettings;
window.scanMidiDevices = scanMidiDevices;
window.updateVolumeLabels = updateVolumeLabels;
window.searchSongs = searchSongs;
window.loadSongsList = loadSongsList;
window.filterSongs = filterSongs;
window.stopSongPlayback = stopSongPlayback;
window.seekSong = seekSong;
window.playSong = playSong;
window.playNextSong = playNextSong;
window.playPrevSong = playPrevSong;
window.togglePlayPause = togglePlayPause;
window.toggleShuffle = toggleShuffle;
window.toggleRepeat = toggleRepeat;
window.toggleRepeaterListening = toggleRepeaterListening;
window.triggerLanguageClassification = triggerLanguageClassification;

// =========================================================================
// PENGENALAN ANGKLUNG & GAME NOT ANGKA ENGINE
// =========================================================================


function switchEduTab(tabName) {
  const tabs = ['sejarah', 'otomatisasi', 'panduan'];
  tabs.forEach(t => {
    const btn = document.getElementById(`edu-tab-${t}`);
    const content = document.getElementById(`edu-content-${t}`);
    if (btn) btn.classList.toggle('active', t === tabName);
    if (content) content.style.display = t === tabName ? 'block' : 'none';
  });
}

// Web MIDI Output Engine (Root Key C: Do = C4 = 60)
let webMidiOutputDevice = null;

function initWebMidiOutput() {
  if (navigator.requestMIDIAccess) {
    navigator.requestMIDIAccess().then(midiAccess => {
      const outputs = Array.from(midiAccess.outputs.values());
      if (outputs.length > 0) {
        webMidiOutputDevice = outputs[0];
        console.log("Web MIDI Output device connected:", webMidiOutputDevice.name);
        const statusElem = document.getElementById('modal-midi-status');
        if (statusElem) statusElem.textContent = `Terhubung (${webMidiOutputDevice.name})`;
      }

      // Web MIDI API Input listener (Direct Browser Keyboard Connection)
      const inputs = Array.from(midiAccess.inputs.values());
      inputs.forEach(input => {
        input.onmidimessage = (event) => {
          if (!isMidiInputPlayAllowed()) return;
          const [status, midiNote, velocity] = event.data;
          const isNoteOn = (status & 0xF0) === 0x90 && velocity > 0;
          if (isNoteOn) {
            const scaleDegree = mapMidiNoteToScaleDegree(midiNote);
            if (scaleDegree && appCurrentPage === 'page-gamenotangka') {
              onGameKeypadPress(scaleDegree);
            }
          }
        };
      });
    }).catch(err => {
      console.warn("Web MIDI Access error:", err);
    });
  }
}

// Call Web MIDI Output Init on page load
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initWebMidiOutput);
} else {
  initWebMidiOutput();
}

function sendMidiNoteOut(scaleDegreeStr, durationMs = 350) {
  const scaleDegreeToMidiNote = {
    '1': 60,  // Do  = C4 (60)
    '2': 62,  // Re  = D4 (62)
    '3': 64,  // Mi  = E4 (64)
    '4': 65,  // Fa  = F4 (65)
    '5': 67,  // Sol = G4 (67)
    '6': 69,  // La  = A4 (69)
    '7': 71,  // Si  = B4 (71)
    "1'": 72, // Do' = C5 (72)
    "8": 72
  };
  const midiNote = scaleDegreeToMidiNote[scaleDegreeStr] || (scaleDegreeStr.includes("'") ? 72 : 60);

  // 1. Send via Web MIDI API to connected Synthesizer / Output Device
  if (webMidiOutputDevice) {
    try {
      webMidiOutputDevice.send([0x90, midiNote, 0x7F]); // Note On
      setTimeout(() => {
        webMidiOutputDevice.send([0x80, midiNote, 0x00]); // Note Off
      }, durationMs);
    } catch(e) {
      console.warn("Web MIDI send error:", e);
    }
  }

  // 2. Send via WebSocket backend if connected
  if (midiSocket && midiSocket.readyState === WebSocket.OPEN) {
    try {
      midiSocket.send(JSON.stringify({
        action: "play_note",
        midi: midiNote,
        pitch: scaleDegreeStr,
        root: "C",
        duration: durationMs
      }));
    } catch(e) {}
  }
}
let lastPlayedGameNoteStr = '1';

// Helper functions for Note items
function getNoteStr(item) {
  let val = typeof item === 'object' ? item.not : item;
  if (val === '7,' || val === '7.') return '0';
  return val;
}

function getNoteLyric(item) {
  return (typeof item === 'object' && item.lyric) ? item.lyric : '';
}

function getSongFlatNotes(song) {
  if (song.notes) return song.notes;
  if (song.phrases) {
    return song.phrases.reduce((acc, p) => acc.concat(p), []);
  }
  return [];
}

// Authentic Not Angka Song Database (. = Titik Perpanjang, 0 = Not Diam, Overline = Garis 1/2 Ketuk)
const GAME_SONGS = [
  {
    title: "Gundul-Gundul Pacul",
    origin: "Jawa Tengah",
    phrases: [
      [
        { not: "1", lyric: "Gun-" },
        { not: "3", lyric: "dul" },
        { not: "1", lyric: "gun-" },
        { not: "3", lyric: "dul" },
        { not: "4", lyric: "pa-" },
        { not: "5", lyric: "cul" },
        { not: "5", lyric: "cul" },
        { not: ".", lyric: "" }
      ],
      [
        { not: "7", lyric: "gem" },
        { not: "1'", lyric: "be" },
        { not: "7", lyric: "" },
        { not: "1'", lyric: "le" },
        { not: "7", lyric: "" },
        { not: "5", lyric: "ngan" },
        { not: ".", lyric: "" }
      ],
      [
        { not: "1", lyric: "Nyung-" },
        { not: "3", lyric: "gi" },
        { not: "1", lyric: "nyung" },
        { not: "3", lyric: "gi" },
        { not: "4", lyric: "wa" },
        { not: "5", lyric: "kul" },
        { not: "5", lyric: "kul" },
        { not: ".", lyric: "" }
      ],
      [
        { not: "7", lyric: "gem" },
        { not: "1'", lyric: "be" },
        { not: "7", lyric: "" },
        { not: "1'", lyric: "le" },
        { not: "7", lyric: "" },
        { not: "5", lyric: "ngan" },
        { not: ".", lyric: "" }
      ],
      [
        { not: "1", lyric: "wa" },
        { not: ".", lyric: "" },
        { not: "3", lyric: "kul" },
        { not: ".", lyric: "" },
        { not: "5", lyric: "ngglim" },
        { not: ".", lyric: "" },
        { not: "4", lyric: "pang" },
        { not: "4", lyric: "se" },
        { not: "5", lyric: "ga" },
        { not: "4", lyric: "ne" },
        { not: "3", lyric: "da" },
        { not: "1", lyric: "di" },
        { not: "4", lyric: "sak" },
        { not: "3", lyric: "la" },
        { not: "1", lyric: "tar" },
        { not: ".", lyric: "" }
      ],
      [
        { not: "1", lyric: "wa" },
        { not: ".", lyric: "" },
        { not: "3", lyric: "kul" },
        { not: ".", lyric: "" },
        { not: "5", lyric: "ngglim" },
        { not: ".", lyric: "" },
        { not: "4", lyric: "pang" },
        { not: "4", lyric: "se" },
        { not: "5", lyric: "ga" },
        { not: "4", lyric: "ne" },
        { not: "3", lyric: "da" },
        { not: "1", lyric: "di" },
        { not: "4", lyric: "sak" },
        { not: "3", lyric: "la" },
        { not: "1", lyric: "tar" },
        { not: ".", lyric: "" }
      ],
    ]
  },
  {
    title: "Manuk Dadali",
    origin: "Jawa Barat",
    phrases: [
      [
        { not: "5", lyric: "Me-" },
        { not: "3", lyric: "at" },
        { not: "4", lyric: "nga-" },
        { not: "5", lyric: "pung" },
        { not: "7", lyric: "lu-" },
        { not: "1'", lyric: "hur" },
        { not: ".", lyric: "" },
        { not: "7", lyric: "ja-" },
        { not: "1'", lyric: "uh" },
        { not: "3", lyric: "di" },
        { not: "4", lyric: "a-" },
        { not: "5", lyric: "wang" },
        { not: "5", lyric: "a-" },
        { not: "5", lyric: "wang" },
        { not: ".", lyric: "" },
      ],
      [
        { not: "5", lyric: "Me-" },
        { not: "3", lyric: "ber" },
        { not: "4", lyric: "ken" },
        { not: "5", lyric: "jang" },
        { not: "7", lyric: "jang" },
        { not: "1'", lyric: "na" },
        { not: ".", lyric: "" },
        { not: "7", lyric: "ba-" },
        { not: "1'", lyric: "ngun" },
        { not: "3", lyric: "ta" },
        { not: "4", lyric: "ya" },
        { not: "3", lyric: "ka" },
        { not: "4", lyric: "ring" },
        { not: "4", lyric: "rang" },
        { not: ".", lyric: "" },
      ],
      [
        { not: "5", lyric: "Ku" },
        { not: "4", lyric: "ku" },
        { not: "3", lyric: "na" },
        { not: "1", lyric: "rang-" },
        { not: "0", lyric: "ga-" },
        { not: "1", lyric: "os" },
        { not: "3", lyric: "reu" },
        { not: "4", lyric: "jeung" },
        { not: "5", lyric: "pa" },
        { not: "1", lyric: "ma" },
        { not: "3", lyric: "tuk" },
        { not: "4", lyric: "na" },
        { not: "4", lyric: "nge-" },
        { not: "4", lyric: "luk" }
      ],
      [
        { not: "5", lyric: "Nga" },
        { not: "4", lyric: "pak" },
        { not: "3", lyric: "me" },
        { not: "1", lyric: "ga" },
        { not: "0", lyric: "ba" },
        { not: "1", lyric: "ri" },
        { not: "3", lyric: "hi" },
        { not: "4", lyric: "ber" },
        { not: "5", lyric: "ne" },
        { not: "1", lyric: "ta" },
        { not: "3", lyric: "rik" },
        { not: "1", lyric: "nyu" },
        { not: "0", lyric: "ru" },
        { not: "1", lyric: "wuk" }
      ]
    ]
  },
  {
    title: "Ibu Kita Kartini",
    origin: "Nasional",
    phrases: [
      [
        { not: "1", lyric: "I-" },
        { not: "2", lyric: "bu" },
        { not: "3", lyric: "Ki-" },
        { not: ".", lyric: "" },
        { not: "4", lyric: "ta", overline: true },
        { not: "5", lyric: "Kar-" },
        { not: "3", lyric: "ti-" },
        { not: "1", lyric: "ni" },
        { not: ".", lyric: "" }
      ],
      [
        { not: "6", lyric: "Put-" },
        { not: "1'", lyric: "ri" },
        { not: "7", lyric: "se-" },
        { not: "6", lyric: "ja-" },
        { not: "5", lyric: "ti" },
        { not: ".", lyric: "" }
      ],
      [
        { not: "4", lyric: "Put-" },
        { not: "6", lyric: "ri" },
        { not: "5", lyric: "In-" },
        { not: "4", lyric: "do-" },
        { not: "3", lyric: "ne-" },
        { not: ".", lyric: "" },
        { not: "1", lyric: "sia" },
        { not: ".", lyric: "" }
      ],
      [
        { not: "2", lyric: "ha-" },
        { not: ".", lyric: "" },
        { not: "4", lyric: "rum" },
        { not: "3", lyric: "na" },
        { not: "2", lyric: "ma" },
        { not: "1", lyric: "nya" },
        { not: ".", lyric: "" },
      ]
    ]
  }
];

let currentSongIdx = 0;
let currentNoteIndex = 0;
let totalHits = 0;
let correctHits = 0;
let isDemoPlaying = false;
let demoTimer = null;

function loadGameSong(idx) {
  if (idx < 0 || idx >= GAME_SONGS.length) return;
  currentSongIdx = idx;
  currentNoteIndex = 0;
  totalHits = 0;
  correctHits = 0;
  if (isDemoPlaying) stopGameDemo();

  const btns = document.querySelectorAll('.game-song-btn');
  btns.forEach((btn, i) => btn.classList.toggle('active', i === idx));

  const song = GAME_SONGS[idx];
  const titleElem = document.getElementById('game-current-song-title');
  const originElem = document.getElementById('game-current-song-origin');
  if (titleElem) titleElem.textContent = song.title;
  if (originElem) originElem.textContent = song.origin;

  updateGameScoreUI();
  renderGameNotSheet();
}

function renderGameNotSheet() {
  const sheet = document.getElementById('game-not-angka-sheet');
  const targetBadge = document.getElementById('game-target-note-badge');
  if (!sheet) return;
  sheet.innerHTML = '';

  const song = GAME_SONGS[currentSongIdx];
  const solfegeNames = { '0': 'Si Rendah', '1': 'Do', '2': 'Re', '3': 'Mi', '4': 'Fa', '5': 'Sol', '6': 'La', '7': 'Si', '1\'': 'Do Tinggi', '.': 'Tahan (.)' };

  const flatNotes = getSongFlatNotes(song);
  let globalNoteIdx = 0;

  const phraseList = song.phrases || [flatNotes];

  phraseList.forEach((phrase) => {
    const phraseRow = document.createElement('div');
    phraseRow.className = 'game-sheet-phrase-row';

    // Left Bar Line (|)
    const leftBar = document.createElement('div');
    leftBar.className = 'sheet-bar-line';
    leftBar.textContent = '|';
    phraseRow.appendChild(leftBar);

    phrase.forEach((item) => {
      const idx = globalNoteIdx;
      globalNoteIdx++;

      const notVal = getNoteStr(item);
      const lyricVal = getNoteLyric(item);
      const isOverline = typeof item === 'object' && item.overline;

      const noteCol = document.createElement('div');
      noteCol.className = 'game-sheet-note-col';
      if (isOverline) noteCol.classList.add('has-overline');
      if (idx === currentNoteIndex) noteCol.classList.add('current');
      else if (idx < currentNoteIndex) noteCol.classList.add('passed');

      noteCol.innerHTML = `
        <div class="sheet-lyric-txt">${lyricVal || '&nbsp;'}</div>
        <div class="sheet-not-num">${notVal}</div>
      `;
      phraseRow.appendChild(noteCol);
    });

    // Right Bar Line (|)
    const rightBar = document.createElement('div');
    rightBar.className = 'sheet-bar-line';
    rightBar.textContent = '|';
    phraseRow.appendChild(rightBar);

    sheet.appendChild(phraseRow);
  });

  if (targetBadge) {
    if (currentNoteIndex < flatNotes.length) {
      let targetIdx = currentNoteIndex;
      while (targetIdx < flatNotes.length - 1 && getNoteStr(flatNotes[targetIdx]) === '.') {
        targetIdx++;
      }
      const targetNotItem = flatNotes[targetIdx];
      const targetNot = getNoteStr(targetNotItem);
      const lyric = getNoteLyric(targetNotItem);

      // Count trailing dots for beat hint
      let dotCount = 0;
      let lookAheadIdx = targetIdx + 1;
      while (lookAheadIdx < flatNotes.length && getNoteStr(flatNotes[lookAheadIdx]) === '.') {
        dotCount++;
        lookAheadIdx++;
      }
      const beatHint = (dotCount > 0) ? ` (${1 + dotCount} Ketuk Panjang)` : '';

      if (targetNot === "1'") {
        targetBadge.textContent = `1' (Do Tinggi)${lyric ? ' - "' + lyric + '"' : ''}${beatHint}`;
      } else if (targetNot === "0") {
        targetBadge.textContent = `0 (Si Rendah)${lyric ? ' - "' + lyric + '"' : ''}${beatHint}`;
      } else {
        const baseNum = targetNot.replace("'", "");
        const sol = solfegeNames[baseNum] || 'Do';
        targetBadge.textContent = `${baseNum} (${sol})${lyric ? ' - "' + lyric + '"' : ''}${beatHint}`;
      }
    } else {
      targetBadge.textContent = "🎉 Selesai! Selamat!";
    }
  }
}

function updateGameScoreUI() {
  const percentElem = document.getElementById('game-score-percent');
  const progressElem = document.getElementById('game-progress-count');
  const song = GAME_SONGS[currentSongIdx];
  const flatNotes = getSongFlatNotes(song);

  let percent = 100;
  if (totalHits > 0) {
    percent = Math.min(100, Math.max(0, Math.round((correctHits / totalHits) * 100)));
  }

  if (percentElem) percentElem.textContent = `${percent}%`;
  if (progressElem) progressElem.textContent = `${currentNoteIndex} / ${flatNotes.length}`;
}

function onGameKeypadPress(numStr) {
  if (appCurrentPage !== 'page-gamenotangka' && appCurrentPage !== 'page-notangka') return;

  const song = GAME_SONGS[currentSongIdx];
  const flatNotes = getSongFlatNotes(song);
  if (currentNoteIndex >= flatNotes.length) return;

  // Skip standalone '.' if currentNoteIndex is on a dot
  while (currentNoteIndex < flatNotes.length - 1 && getNoteStr(flatNotes[currentNoteIndex]) === '.') {
    currentNoteIndex++;
  }

  const targetItem = flatNotes[currentNoteIndex];
  const rawTargetNot = getNoteStr(targetItem); // e.g. "1'", "5", "0"

  // 1. Calculate trailing dots '.' following this note
  let dotCount = 0;
  let lookAheadIdx = currentNoteIndex + 1;
  while (lookAheadIdx < flatNotes.length && getNoteStr(flatNotes[lookAheadIdx]) === '.') {
    dotCount++;
    lookAheadIdx++;
  }

  const totalBeats = 1 + dotCount;
  const durationMs = 350 + (dotCount * 350); // e.g. 1 dot = 700ms, 2 dots = 1050ms

  // 2. Send MIDI Output Note with sustained duration
  sendMidiNoteOut(numStr, durationMs);

  // 3. Trigger Angklung Hardware & Web Audio Synth with sustained duration
  const scaleDegreeToPitch = { 
    '0': 'B3',
    '1': 'C4', 
    '2': 'D4', 
    '3': 'E4', 
    '4': 'F4', 
    '5': 'G4', 
    '6': 'A4', 
    '7': 'B4', 
    "1'": 'C5', 
    "8": 'C5' 
  };
  const pitchName = scaleDegreeToPitch[numStr] || (numStr.includes("'") ? 'C5' : 'C4');
  let hw = mapPitchNameToNoteNumber(pitchName);
  if (hw) {
    highlightKeyProgrammatic(hw.note, hw.angklung, true, durationMs);
    playChordForNoteNumSustained(hw.note, hw.angklung, durationMs);
  }

  // 4. Check match & update score counters
  if (numStr === rawTargetNot) {
    totalHits += totalBeats;
    correctHits += totalBeats;
    currentNoteIndex += totalBeats;
  } else {
    totalHits += 1;
  }

  updateGameScoreUI();
  renderGameNotSheet();
}

function toggleGameDemoAutoPlay() {
  if (isDemoPlaying) {
    stopGameDemo();
  } else {
    startGameDemo();
  }
}

function startGameDemo() {
  isDemoPlaying = true;
  currentNoteIndex = 0;
  const demoBtn = document.getElementById('game-demo-btn');
  if (demoBtn) demoBtn.textContent = "⏹️ Stop Demo";

  const song = GAME_SONGS[currentSongIdx];
  const flatNotes = getSongFlatNotes(song);

  const playStep = () => {
    if (!isDemoPlaying || currentNoteIndex >= flatNotes.length) {
      stopGameDemo();
      return;
    }

    const notItem = flatNotes[currentNoteIndex];
    const notStr = getNoteStr(notItem);
    onGameKeypadPress(notStr);
    demoTimer = setTimeout(playStep, 500);
  };

  playStep();
}

function stopGameDemo() {
  isDemoPlaying = false;
  if (demoTimer) clearTimeout(demoTimer);
  const demoBtn = document.getElementById('game-demo-btn');
  if (demoBtn) demoBtn.textContent = "▶ Demo Auto-Play";
}

function resetGameSession() {
  stopGameDemo();
  currentNoteIndex = 0;
  totalHits = 0;
  correctHits = 0;
  updateGameScoreUI();
  renderGameNotSheet();
}

// Keyboard listener for Game Not Angka (Keys 1-8)
document.addEventListener('keydown', (e) => {
  const activePage = document.querySelector('.app-page:not(.hide)');
  if (activePage && activePage.id === 'page-gamenotangka') {
    if (['0', '1', '2', '3', '4', '5', '6', '7'].includes(e.key)) {
      onGameKeypadPress(e.key);
    } else if (e.key === '8' || e.key === '!') {
      onGameKeypadPress("1'");
    }
  }
});

// Initialize first game song on page load
document.addEventListener('DOMContentLoaded', () => {
  loadGameSong(0);
});

window.loadGameSong = loadGameSong;
window.onGameKeypadPress = onGameKeypadPress;
window.toggleGameDemoAutoPlay = toggleGameDemoAutoPlay;
window.resetGameSession = resetGameSession;
window.handleGlobalSearch = handleGlobalSearch;
window.nextMenu = nextMenu;
window.prevMenu = prevMenu;
window.playBahasaSong = playBahasaSong;
window.resetBahasaPage = resetBahasaPage;
window.setBahasaMode = setBahasaMode;

// ====================================================================
// 13. Agen Rima Logic (Voice Assistant)
// ====================================================================
let voiceSearchState = 0; // 0: Idle, 1: Main Menu, 2: Category, 3: Song
let voiceSearchRecognition = null;
let currentVoiceCategory = null;
let isAgenModeActive = false;

function initVoiceSearch() {
  if (voiceSearchRecognition) return;
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRecognition) {
    alert("Browser Anda tidak mendukung Web Speech API. Silakan gunakan Google Chrome.");
    return;
  }
  voiceSearchRecognition = new SpeechRecognition();
  voiceSearchRecognition.lang = 'id-ID';
  voiceSearchRecognition.interimResults = false;
  voiceSearchRecognition.maxAlternatives = 1;

  voiceSearchRecognition.onstart = function() {
    const indicator = document.getElementById('voice-listening-indicator');
    if (indicator) indicator.style.display = 'block';
  };

  voiceSearchRecognition.onend = function() {
    const indicator = document.getElementById('voice-listening-indicator');
    if (indicator) indicator.style.display = 'none';
    
    // Auto-restart if mode is active and not currently speaking
    if (isAgenModeActive && !window.speechSynthesis.speaking) {
       try { voiceSearchRecognition.start(); } catch(e){}
    }
  };

  voiceSearchRecognition.onerror = function(event) {
    console.error("Speech recognition error:", event.error);
    const indicator = document.getElementById('voice-listening-indicator');
    if (indicator) indicator.style.display = 'none';
  };

  voiceSearchRecognition.onresult = function(event) {
    if (event.results.length === 0) return;
    const transcript = event.results[0][0].transcript.trim().toLowerCase();
    console.log("Agen Rima Transcript:", transcript);

    if (isAgenModeActive) {
      if (transcript.includes("matikan agen") || transcript.includes("keluar mode")) {
         stopAgenRima();
         return;
      }
      if (transcript.includes("menu utama")) {
         voiceSearchState = 1;
         speakText("Anda berada di Menu Utama. Anda dapat langsung menyebutkan judul lagu, atau memilih menu: Pustaka Lagu, Kontrol Manual, Repeater, Deteksi Bahasa, Pengenalan Angklung, atau Game Not Angka.", () => { if(isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} } });
         return;
      }
      if (transcript.includes("hi rima") || transcript.includes("hai rima")) {
         voiceSearchState = 1; // Kembali ke state 1 agar bisa mendengarkan lagu/menu bebas
         speakText("Ya? Silakan.", () => { if(isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} } });
         return;
      }
      if (transcript.includes("stop lagu") || transcript.includes("berhenti lagu") || transcript.includes("matikan lagu")) {
         stopSongPlayback();
         voiceSearchState = 0;
         speakText("Pemutaran lagu telah dihentikan.", () => { if(isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} } });
         return;
      }
      if (transcript.includes("ganti lagu")) {
         stopSongPlayback();
         if (currentVoiceCategory) {
            voiceSearchState = 3;
            speakText("Pemutaran lagu telah dihentikan. Silakan sebutkan judul lagu baru yang ingin dimainkan.", () => { if(isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} } });
         } else {
            voiceSearchState = 1;
            speakText("Pemutaran lagu dihentikan.", () => { if(isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} } });
         }
         return;
      }
      if (transcript.includes("ganti kategori")) {
         stopSongPlayback();
         if (voiceSearchState === 3 || voiceSearchState === 2 || voiceSearchState === 0) {
            voiceSearchState = 2;
            const categoriesSet = new Set(songs.map(s => s.folder));
            const categoryList = Array.from(categoriesSet).join(', ');
            speakText(`Kembali ke pemilihan kategori. Kategori yang tersedia adalah: ${categoryList}. Silakan sebutkan nama kategori.`, () => { if(isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} } });
            return;
         }
      }
      if (transcript.includes("kembali") || transcript.includes("balik")) {
         stopSongPlayback();
         const prevPage = navigateBack();
         if (prevPage) {
            voiceSearchState = (prevPage === 'page-pustaka') ? 2 : 1;
            const pageName = PAGE_NAMES[prevPage] || 'menu sebelumnya';
            let msg = `Kembali ke menu ${pageName}.`;
            if (prevPage === 'page-pustaka') {
               const categoriesSet = new Set(songs.map(s => s.folder));
               const categoryList = Array.from(categoriesSet).join(', ');
               msg += ` Kategori yang tersedia adalah: ${categoryList}.`;
            }
            speakText(msg, () => { if(isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} } });
         } else {
            speakText("Tidak ada menu sebelumnya.", () => { if(isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} } });
         }
         return;
      }
    }

    if (voiceSearchState === 1) {
      handleVoiceMenuSearch(transcript);
    } else if (voiceSearchState === 2) {
      handleVoiceCategorySearch(transcript);
    } else if (voiceSearchState === 3) {
      handleVoiceSongInCategorySearch(transcript);
    }
  };
}

function speakText(text, callback) {
  if (!window.speechSynthesis) {
    if (callback) callback();
    return;
  }
  
  if (voiceSearchRecognition) {
      try { voiceSearchRecognition.abort(); } catch(e){}
  }
  
  window.speechSynthesis.cancel();
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.lang = 'id-ID';
  utterance.onend = function() {
    if (callback) callback();
    else if (isAgenModeActive) {
      try { voiceSearchRecognition.start(); } catch(e){}
    }
  };
  window.speechSynthesis.speak(utterance);
}

window.startAgenRima = function() {
  initVoiceSearch();
  if (!voiceSearchRecognition) return;
  
  if (isAgenModeActive) {
     stopAgenRima();
     return;
  }
  
  isAgenModeActive = true;
  voiceSearchState = 1;
  
  const fab = document.getElementById('agen-rima-fab');
  if (fab) fab.style.background = 'linear-gradient(135deg, #22c55e 0%, #16a34a 100%)';
  
  speakText("Selamat datang di Agen Rima. Anda dapat langsung menyebutkan judul lagu yang ingin diputar, atau memilih menu: Pustaka Lagu, Kontrol Manual, Repeater, Deteksi Bahasa, Pengenalan Angklung, atau Game Not Angka. Apa yang ingin Anda lakukan?", () => {
    try { voiceSearchRecognition.start(); } catch(e){}
  });
};

function stopAgenRima() {
  isAgenModeActive = false;
  voiceSearchState = 0;
  
  const fab = document.getElementById('agen-rima-fab');
  if (fab) fab.style.background = 'linear-gradient(135deg, #a855f7 0%, #6b21a8 100%)';
  
  if (voiceSearchRecognition) {
    try { voiceSearchRecognition.abort(); } catch(e){}
  }
  window.speechSynthesis.cancel();
  speakText("Agen Rima dimatikan. Terima kasih telah menggunakan layanan kami.", () => {});
}

function handleVoiceMenuSearch(query) {
  // Cek apakah query langsung menyebutkan judul lagu secara global
  const matchedSong = songs.find(s => s.title.toLowerCase().includes(query) || query.includes(s.title.toLowerCase()));
  if (matchedSong) {
    voiceSearchState = 0; // go to idle
    navigateTo('page-pustaka');
    const searchInput = document.getElementById('cn-search-input');
    if (searchInput) searchInput.value = '';
    
    speakText(`Memainkan lagu ${matchedSong.title}.`, () => {
      setTimeout(() => playSong(matchedSong.id, matchedSong.notes), 400);
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
    return;
  }

  // Cek apakah query langsung menyebutkan nama kategori
  const categoriesSet = new Set(songs.map(s => s.folder));
  const matchedCategory = Array.from(categoriesSet).find(c => c.toLowerCase().includes(query) || query.includes(c.toLowerCase()));
  if (matchedCategory) {
    navigateTo('page-pustaka');
    currentVoiceCategory = matchedCategory;
    const songsInCategory = songs.filter(s => s.folder === matchedCategory);
    const songList = songsInCategory.map(s => s.title).join(', ');
    
    voiceSearchState = 3;
    speakText(`Kategori ${matchedCategory} dipilih. Lagu yang tersedia adalah: ${songList}. Silakan sebutkan judul lagu.`, () => {
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
    return;
  }
  if (query.includes('pustaka') || query.includes('lagu') || query.includes('mendengarkan')) {
    navigateTo('page-pustaka');
    const categoriesSet = new Set(songs.map(s => s.folder));
    const categoriesArray = Array.from(categoriesSet);
    const categoryList = categoriesArray.join(', ');
    
    voiceSearchState = 2;
    speakText(`Anda telah memasuki menu Pustaka Lagu. Kategori yang tersedia adalah: ${categoryList}. Silakan sebutkan nama kategori yang ingin Anda dengarkan.`, () => {
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
  } else if (query.includes('kontrol') || query.includes('manual') || query.includes('main')) {
    voiceSearchState = 0;
    navigateTo('page-manual');
    speakText("Anda telah memasuki menu Kontrol Manual. Di sini Anda bisa memainkan angklung menggunakan keyboard yang tersedia.", () => {
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
  } else if (query.includes('repeater') || query.includes('irama') || query.includes('ikuti')) {
    voiceSearchState = 0;
    navigateTo('page-repeater');
    speakText("Anda telah memasuki menu Repeater. Di sini Anda bisa mendengarkan dan mengikuti irama secara bertahap.", () => {
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
  } else if (query.includes('pengenalan') || query.includes('sejarah') || query.includes('bambu')) {
    voiceSearchState = 0;
    navigateTo('page-pengenalan');
    speakText("Anda telah memasuki menu Edukasi Angklung. Angklung merupakan kebanggaan masyarakat Sunda, yang berasal dari kata angkleung-angkleungan. Diakui oleh UNESCO pada 16 November 2010 sebagai Warisan Budaya Takbenda Dunia. Pada tahun 1938, Daeng Soetigna berinovasi menciptakan Angklung Padaeng dengan tangga nada diatonik. Bahan pembuatannya menggunakan Bambu Wulung untuk nada bas dan Bambu Tali untuk nada tinggi. Terdapat 3 teknik memainkannya yaitu Kurulung atau getar, Centok atau hentak, dan Tepuk. Pada Otomatisasi Project RIMA, hardware yang digunakan adalah Solenoid Actuator 12 Volt, Mikrokontroler ESP32, dan Multi-Channel Relay Driver. Cara kerja sistem AI-nya, Mikrofon merekam vokal pengguna secara real-time, AI PyTorch mengestimasi frekuensi nada, lalu disetel ke tangga nada harmonis dan dikirimkan sinyalnya untuk menggetarkan angklung fisik. Untuk panduan aplikasi, terdapat fitur Pustaka Lagu untuk memainkan lagu daerah, Kontrol Manual untuk memainkan angklung dengan tuts piano, Repeater Vokal agar robot meniru nyanyian Anda, Game Not Angka untuk melatih ritme, dan Deteksi Bahasa untuk merekomendasikan lagu berdasarkan kata sapaan daerah.", () => {
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
  } else if (query.includes('game') || query.includes('not angka') || query.includes('latihan')) {
    voiceSearchState = 0;
    navigateTo('page-gamenotangka');
    speakText("Anda telah memasuki menu Game Not Angka. Di sini Anda dapat melatih akurasi ritme dengan menekan tuts not angka interaktif.", () => {
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
  } else if (query.includes('deteksi') || query.includes('bahasa') || query.includes('perintah')) {
    navigateTo('page-bahasa');
    
    // Matikan mode agen rima agar tidak bentrok dengan listening engine deteksi bahasa
    isAgenModeActive = false;
    voiceSearchState = 0;
    const fab = document.getElementById('agen-rima-fab');
    if (fab) fab.style.background = 'linear-gradient(135deg, #a855f7 0%, #6b21a8 100%)';
    if (voiceSearchRecognition) {
      try { voiceSearchRecognition.abort(); } catch(e){}
    }
    window.speechSynthesis.cancel();

    speakText("Anda telah memasuki menu Deteksi Bahasa. Kata yang bisa Anda sebutkan adalah: Sampurasun, Horas, Adil Ka Talino, Wa Wa Wa, Kula Nuwun, Tabea, dan Peuhaba. Silakan tekan ikon mikrofon di tengah layar untuk memulai deteksi.", () => {});
  } else {
    speakText("Maaf, perintah, menu, atau lagu tersebut tidak ditemukan. Silakan sebutkan nama menu, kategori, atau judul lagu yang Anda inginkan.", () => {
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
  }
}

function handleVoiceCategorySearch(query) {
  const categoriesSet = new Set(songs.map(s => s.folder));
  const matchedCategory = Array.from(categoriesSet).find(c => c.toLowerCase().includes(query) || query.includes(c.toLowerCase()));
  
  if (matchedCategory) {
    currentVoiceCategory = matchedCategory;
    const songsInCategory = songs.filter(s => s.folder === matchedCategory);
    const songList = songsInCategory.map(s => s.title).join(', ');
    
    voiceSearchState = 3;
    speakText(`Lagu di kategori ${matchedCategory} adalah: ${songList}. Silakan sebutkan judul lagu yang ingin dimainkan.`, () => {
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
  } else {
    // Fallback: Jika tidak cocok dengan kategori, coba cari sebagai menu atau lagu
    handleVoiceMenuSearch(query);
  }
}

function handleVoiceSongInCategorySearch(query) {
  const songsInCategory = songs.filter(s => s.folder === currentVoiceCategory);
  const matchedSong = songsInCategory.find(s => s.title.toLowerCase().includes(query) || query.includes(s.title.toLowerCase()));
  
  if (matchedSong) {
    voiceSearchState = 0; // go to idle
    const searchInput = document.getElementById('cn-search-input');
    if (searchInput) searchInput.value = '';
    setTimeout(() => playSong(matchedSong.id, matchedSong.notes), 400);
  } else {
    speakText(`Lagu tidak ada di kategori ${currentVoiceCategory}. Silakan sebutkan ulang judul lagu.`, () => {
      if (isAgenModeActive) { try { voiceSearchRecognition.start(); } catch(e){} }
    });
  }
}

// Flexible Draggable Agen Rima FAB with Edge Snapping
function initDraggableAgenRimaFab() {
  const container = document.getElementById('agen-rima-fab-container');
  const btn = document.getElementById('agen-rima-fab');
  if (!container || !btn) return;

  let isDragging = false;
  let startX = 0, startY = 0;
  let initialLeft = 0, initialTop = 0;
  let dragDistance = 0;
  const padding = 20;

  // Restore saved position from localStorage if available
  const savedPos = localStorage.getItem('rima_fab_pos');
  if (savedPos) {
    try {
      const pos = JSON.parse(savedPos);
      let left = pos.left;
      let top = pos.top;
      const maxLeft = window.innerWidth - container.offsetWidth - padding;
      const maxTop = window.innerHeight - container.offsetHeight - padding;
      left = Math.max(padding, Math.min(left, maxLeft));
      top = Math.max(padding, Math.min(top, maxTop));

      container.style.left = `${left}px`;
      container.style.top = `${top}px`;
      container.style.bottom = 'auto';
      container.style.right = 'auto';
    } catch (_) {}
  } else {
    const defaultLeft = padding;
    const defaultTop = window.innerHeight - (container.offsetHeight || 100) - 50;
    container.style.left = `${defaultLeft}px`;
    container.style.top = `${defaultTop}px`;
    container.style.bottom = 'auto';
    container.style.right = 'auto';
  }

  function snapToNearestEdge() {
    const rect = container.getBoundingClientRect();
    const width = rect.width || 100;
    const height = rect.height || 100;

    const centerX = rect.left + width / 2;

    const snapLeft = centerX < window.innerWidth / 2 ? padding : (window.innerWidth - width - padding);
    let snapTop = rect.top;

    const minTop = padding;
    const maxTop = window.innerHeight - height - padding;
    snapTop = Math.max(minTop, Math.min(snapTop, maxTop));

    container.style.transition = 'left 0.35s cubic-bezier(0.2, 0.8, 0.2, 1), top 0.35s cubic-bezier(0.2, 0.8, 0.2, 1)';
    container.style.left = `${snapLeft}px`;
    container.style.top = `${snapTop}px`;
    container.style.bottom = 'auto';
    container.style.right = 'auto';

    localStorage.setItem('rima_fab_pos', JSON.stringify({ left: snapLeft, top: snapTop }));
  }

  function onPointerDown(e) {
    isDragging = true;
    dragDistance = 0;
    const clientX = e.touches ? e.touches[0].clientX : e.clientX;
    const clientY = e.touches ? e.touches[0].clientY : e.clientY;

    startX = clientX;
    startY = clientY;

    const rect = container.getBoundingClientRect();
    initialLeft = rect.left;
    initialTop = rect.top;

    container.style.transition = 'none';

    window.addEventListener('mousemove', onPointerMove);
    window.addEventListener('touchmove', onPointerMove, { passive: false });
    window.addEventListener('mouseup', onPointerUp);
    window.addEventListener('touchend', onPointerUp);
  }

  function onPointerMove(e) {
    if (!isDragging) return;
    const clientX = e.touches ? e.touches[0].clientX : e.clientX;
    const clientY = e.touches ? e.touches[0].clientY : e.clientY;

    const deltaX = clientX - startX;
    const deltaY = clientY - startY;

    dragDistance = Math.hypot(deltaX, deltaY);

    if (dragDistance > 5) {
      if (e.cancelable) e.preventDefault();
      let newLeft = initialLeft + deltaX;
      let newTop = initialTop + deltaY;

      const maxLeft = window.innerWidth - container.offsetWidth;
      const maxTop = window.innerHeight - container.offsetHeight;

      newLeft = Math.max(0, Math.min(newLeft, maxLeft));
      newTop = Math.max(0, Math.min(newTop, maxTop));

      container.style.left = `${newLeft}px`;
      container.style.top = `${newTop}px`;
      container.style.bottom = 'auto';
      container.style.right = 'auto';
    }
  }

  function onPointerUp(e) {
    if (!isDragging) return;
    isDragging = false;

    window.removeEventListener('mousemove', onPointerMove);
    window.removeEventListener('touchmove', onPointerMove);
    window.removeEventListener('mouseup', onPointerUp);
    window.removeEventListener('touchend', onPointerUp);

    if (dragDistance > 5) {
      snapToNearestEdge();
    }
  }

  btn.addEventListener('click', (e) => {
    if (dragDistance > 5) {
      e.stopImmediatePropagation();
      e.preventDefault();
      dragDistance = 0;
      return false;
    }
  }, true);

  btn.addEventListener('mousedown', onPointerDown);
  btn.addEventListener('touchstart', onPointerDown, { passive: true });

  window.addEventListener('resize', () => {
    snapToNearestEdge();
  });
}