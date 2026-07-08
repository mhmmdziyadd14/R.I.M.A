// ====================================================================
// R.I.M.A SPA WEB CONTROLLER APPLICATION LOGIC
// ====================================================================

let settings = {
  port1: localStorage.getItem('rima_port_1') || 'COM10',
  port2: localStorage.getItem('rima_port_2') || 'COM11',
  port3: localStorage.getItem('rima_port_3') || 'COM12',
  aiApi: localStorage.getItem('rima_ai_api') || 'http://192.168.1.100:8001',
  localApi: 'http://localhost:8000',
  simulationMode: localStorage.getItem('rima_simulation_mode') === null ? true : localStorage.getItem('rima_simulation_mode') === 'true'
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
    9: 1396.91, 10: 1479.98, 11: 1567.98, 12: 1661.22
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

let activeSongInterval = null;
let repeaterSocket = null;
let isRepeaterListening = false;
let audioContext = null;
let micStream = null;
let scriptProcessor = null;
let keyIntervals = new Map();
let chordIntervals = new Map();

function startKeyTrigger(keyElement) {
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
    
    document.getElementById('active-note-display').textContent = label.toUpperCase();

    // Play local synthesizer sound
    const freqMap = NOTE_FREQUENCIES[angklungId];
    if (freqMap && freqMap[noteNum]) {
      playClientSynthSound(freqMap[noteNum]);
    }

    // Send to python serial endpoint
    fetch(`${settings.localApi}/api/arduino/play?note=${noteNum}&angklung_id=${angklungId}`).catch(() => {});
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

// 3. Application Startup
document.addEventListener('DOMContentLoaded', () => {
  // Set initial settings values to modal inputs
  document.getElementById('input-com-port-1').value = settings.port1;
  document.getElementById('input-com-port-2').value = "Terintegrasi dengan Angklung 1";
  document.getElementById('input-com-port-3').value = settings.port3;
  document.getElementById('input-host-api').value = settings.aiApi;

  // Initialize view and run background connection checks
  loadSongsFromBackend();
  checkConnections();
  setInterval(checkConnections, 6000); // Check connections every 6 seconds

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
function navigateTo(pageId) {
  // Clear any running song playbacks or socket connections when switching pages
  stopAllPlaybacks();

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
  const localHost = settings.localApi;
  const aiHost = settings.aiApi;

  // Check python FastAPI status
  let isApiOnline = false;
  try {
    const response = await fetch(`${localHost}/api/health`, { method: 'GET' });
    if (response.ok) isApiOnline = true;
  } catch (_) {}

  // Check serial com status on python for all 3 devices
  let statuses = { angklung1: 'offline', angklung2: 'offline', angklung3: 'offline' };
  if (isApiOnline) {
    try {
      // Sync configurations to python backend
      await fetch(`${localHost}/api/config-arduino`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          port1: settings.port1,
          port2: settings.port2,
          port3: settings.port3,
          simulation_mode: settings.simulationMode
        })
      });

      const response = await fetch(`${localHost}/api/arduino/status`);
      if (response.ok) {
        const data = await response.json();
        statuses.angklung1 = data.angklung1.status;
        statuses.angklung2 = data.angklung2.status;
        statuses.angklung3 = data.angklung3.status;
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

// Settings Overlay Handlers
function toggleSettingsModal() {
  const modal = document.getElementById('settings-modal');
  if (!modal.classList.contains('active')) {
    document.getElementById('input-com-port-1').value = settings.port1;
    document.getElementById('input-com-port-3').value = settings.port3;
    document.getElementById('input-host-api').value = settings.aiApi;
    document.getElementById('input-simulation-mode').checked = settings.simulationMode;
  }
  modal.classList.toggle('active');
}

function saveConnectionSettings() {
  const p1 = document.getElementById('input-com-port-1').value.trim();
  const p3 = document.getElementById('input-com-port-3').value.trim();
  const hostVal = document.getElementById('input-host-api').value.trim();
  const simMode = document.getElementById('input-simulation-mode').checked;

  settings.port1 = p1;
  settings.port2 = p1; // Share same port with Angklung 1
  settings.port3 = p3;
  settings.aiApi = hostVal;
  settings.simulationMode = simMode;

  localStorage.setItem('rima_port_1', p1);
  localStorage.setItem('rima_port_2', p1);
  localStorage.setItem('rima_port_3', p3);
  localStorage.setItem('rima_ai_api', hostVal);
  localStorage.setItem('rima_simulation_mode', simMode);

  toggleSettingsModal();
  checkConnections();
}

// 6. Interactive Keyboard Playback
function triggerKeyOn(keyElement) {
  const noteNum = parseInt(keyElement.getAttribute('data-note'), 10);
  const label = keyElement.getAttribute('data-label');
  const angklungId = parseInt(keyElement.getAttribute('data-angklung') || '3', 10);
  
  // Show active visual trigger
  keyElement.classList.add('active');
  document.getElementById('active-note-display').textContent = label.toUpperCase();

  // Play client-side audio synth instantly
  const freqMap = NOTE_FREQUENCIES[angklungId];
  if (freqMap && freqMap[noteNum]) {
    playClientSynthSound(freqMap[noteNum]);
  }

  // Send request to python backend
  fetch(`${settings.localApi}/api/arduino/play?note=${noteNum}&angklung_id=${angklungId}`).catch(() => {});

  // Remove active visual after transient delay
  setTimeout(() => {
    keyElement.classList.remove('active');
  }, 250);
}

// Programmatic key highlight (for repeater incoming feedback & song playbacks)
function highlightKeyProgrammatic(noteNum, angklungId = 3) {
  const key = document.querySelector(`.key[data-note="${noteNum}"][data-angklung="${angklungId}"]`);
  if (key) {
    key.classList.add('active');
    document.getElementById('active-note-display').textContent = key.getAttribute('data-label').toUpperCase();
    
    const freqMap = NOTE_FREQUENCIES[angklungId];
    if (freqMap && freqMap[noteNum]) {
      playClientSynthSound(freqMap[noteNum]);
    }
    
    setTimeout(() => {
      key.classList.remove('active');
    }, 200);
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
  "g#6": { angklung: 2, note: 12 }
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

// 7. Chord Triggering (C, Cm, C#, C#m, ... B, Bm)
function playChord(chordName) {
  let rootName = chordName;
  let isMinor = false;
  
  if (chordName.endsWith('m')) {
    rootName = chordName.slice(0, -1);
    isMinor = true;
  }
  
  const rootMap = {
    'C': 60, 'C#': 61, 'Db': 61, 'D': 62, 'D#': 63, 'Eb': 63,
    'E': 64, 'F': 65, 'F#': 66, 'Gb': 66, 'G': 67, 'G#': 68,
    'Ab': 68, 'A': 69, 'A#': 70, 'Bb': 70, 'B': 71
  };
  
  const rootMidi = rootMap[rootName];
  if (!rootMidi) return;
  
  const thirdOffset = isMinor ? 3 : 4;
  const fifthOffset = 7;
  
  const melodyNotes = [rootMidi, rootMidi + thirdOffset, rootMidi + fifthOffset];
  
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
  fetch(`${settings.localApi}/api/arduino/play_multi?a1=${a1Param}&a3=${a3Param}`).catch(() => {});
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
function loadSongsList(filter = 'all') {
  const container = document.getElementById('songs-container');
  container.innerHTML = '';

  const filtered = filter === 'all' ? songs : songs.filter(s => s.category === filter);

  filtered.forEach(song => {
    // Escape single quotes for HTML onClick
    const cleanId = song.id.replace(/'/g, "\\'");
    const btnDomId = getSongBtnId(song.id);
    const item = document.createElement('div');
    item.className = 'song-item';
    item.innerHTML = `
      <div class="song-info">
        <div class="song-icon"><i class="fa-solid fa-music"></i></div>
        <div class="song-details">
          <h4>${song.title}</h4>
          <p>${song.region}</p>
        </div>
      </div>
      <button class="song-play-btn" id="${btnDomId}" onclick="playSong('${cleanId}')">
        <i class="fa-solid fa-play"></i>
      </button>
    `;
    container.appendChild(item);
  });
}

// 8.1 Backend Song Loader
async function loadSongsFromBackend() {
  try {
    const response = await fetch(`${settings.localApi}/api/songs`);
    if (response.ok) {
      const backendSongs = await response.json();
      songs = backendSongs.map(s => ({
        id: s.file_name,
        title: s.title,
        region: s.region,
        file_name: s.file_name,
        category: s.region.toLowerCase().includes('sunda') ? 'sunda' :
                  s.region.toLowerCase().includes('jawa') ? 'jawa' :
                  s.region.toLowerCase().includes('papua') ? 'papua' : 'all'
      }));
      loadSongsList();
    }
  } catch (e) {
    console.error("Gagal mengambil daftar lagu dari backend:", e);
  }
}

function filterSongs(category) {
  const buttons = document.querySelectorAll('.tag-btn');
  buttons.forEach(btn => btn.classList.remove('active'));
  event.target.classList.add('active');

  loadSongsList(category);
}

async function playSong(songId) {
  const playBtn = document.getElementById(getSongBtnId(songId));
  
  if (playBtn && playBtn.classList.contains('playing')) {
    stopAllPlaybacks();
    return;
  }

  stopAllPlaybacks();
  if (playBtn) {
    playBtn.classList.add('playing');
    playBtn.innerHTML = '<i class="fa-solid fa-stop"></i>';
  }

  try {
    const response = await fetch(`${settings.localApi}/api/arduino/play_song_file`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ file_name: songId })
    });
    if (!response.ok) {
      alert("Gagal memutar lagu di server.");
      stopAllPlaybacks();
    }
  } catch (e) {
    alert("Gagal menghubungi server.");
    stopAllPlaybacks();
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
      const response = await fetch(`${settings.localApi}/api/arduino/play_song_file`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ file_content: text })
      });
      
      if (response.ok) {
        console.log("[PLAYER] Memulai pemutaran file .123 di server.");
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
  fetch(`${settings.localApi}/api/arduino/stop_song`)
    .then(() => {
      console.log("[PLAYER] Menghentikan pemutaran lagu di server.");
    })
    .catch(() => {});
}

// 9. Repeater Section (Pitch Tuning via Websocket)
async function toggleRepeaterListening() {
  const micBtn = document.getElementById('mic-repeater-btn');
  const sonar = document.querySelector('.sonar-wave.wave-green');
  const statusText = document.getElementById('repeater-status');

  if (isRepeaterListening) {
    stopAllPlaybacks();
    return;
  }

  isRepeaterListening = true;
  micBtn.classList.add('active');
  sonar.classList.add('active');
  statusText.textContent = 'Meminta izin mikrofon...';

  try {
    micStream = await navigator.mediaDevices.getUserMedia({ audio: true, video: false });
    statusText.textContent = 'Mendengarkan nada... Dekatkan angklung ke mikrofon!';
    
    const wsHost = settings.aiApi.replace('http://', 'ws://');
    repeaterSocket = new WebSocket(`${wsHost}/ws/pitch`);
    
    repeaterSocket.onopen = () => {
      audioContext = new (window.AudioContext || window.webkitAudioContext)();
      const source = audioContext.createMediaStreamSource(micStream);
      
      // Send sample rate meta
      repeaterSocket.send(JSON.stringify({ sampleRate: audioContext.sampleRate }));
      
      scriptProcessor = audioContext.createScriptProcessor(2048, 1, 1);
      source.connect(scriptProcessor);
      scriptProcessor.connect(audioContext.destination);
      
      scriptProcessor.onaudioprocess = (e) => {
        if (repeaterSocket.readyState === WebSocket.OPEN) {
          const inputData = e.inputBuffer.getChannelData(0);
          repeaterSocket.send(inputData.buffer); // Float32Array PCM
        }
      };
    };

    repeaterSocket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      if (data.frequency > 0) {
        document.getElementById('repeater-note').textContent = data.note;
        document.getElementById('repeater-freq').textContent = `${data.frequency.toFixed(1)} Hz`;
        if (data.note) {
          const matchedNote = mapPitchNameToNoteNumber(data.note);
          if (matchedNote) highlightKeyProgrammatic(matchedNote);
        }
      }
    };

    repeaterSocket.onclose = () => {
      if (isRepeaterListening) stopAllPlaybacks();
    };
  } catch (e) {
    console.error(e);
    statusText.textContent = 'Gagal mengakses mikrofon browser.';
    stopAllPlaybacks();
  }
}
// Maps incoming WebSocket pitch names back to 1-16 note keys
function mapPitchNameToNoteNumber(pitchName) {
  const map = {
    'C3': 1, 'C#3': 2, 'D3': 3, 'D#3': 4, 'E3': 5, 'F3': 6, 'F#3': 7, 'G3': 8,
    'E2': 9, 'F2': 10, 'F#2': 11, 'G2': 12, 'G#2': 13, 'A2': 14, 'A#2': 15, 'B2': 16
  };
  return map[pitchName.toUpperCase()] || null;
}

// 10. Language Classification (AI Perekam)
// Simple WAV Encoder
function encodeWAV(samples, sampleRate) {
  const buffer = new ArrayBuffer(44 + samples.length * 2);
  const view = new DataView(buffer);
  
  const writeString = (view, offset, string) => {
    for (let i = 0; i < string.length; i++) {
      view.setUint8(offset + i, string.charCodeAt(i));
    }
  };
  
  writeString(view, 0, 'RIFF');
  view.setUint32(4, 36 + samples.length * 2, true);
  writeString(view, 8, 'WAVE');
  writeString(view, 12, 'fmt ');
  view.setUint32(16, 16, true);
  view.setUint16(20, 1, true); 
  view.setUint16(22, 1, true); 
  view.setUint32(24, sampleRate, true);
  view.setUint32(28, sampleRate * 2, true);
  view.setUint16(32, 2, true);
  view.setUint16(34, 16, true);
  writeString(view, 36, 'data');
  view.setUint32(40, samples.length * 2, true);
  
  let offset = 44;
  for (let i = 0; i < samples.length; i++, offset += 2) {
    let s = Math.max(-1, Math.min(1, samples[i]));
    view.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
  }
  return new Blob([view], { type: 'audio/wav' });
}

async function triggerLanguageClassification() {
  const micBtn = document.getElementById('mic-bahasa-btn');
  const sonar = document.getElementById('ai-waves');
  const statusText = document.getElementById('ai-status');

  micBtn.disabled = true;
  micBtn.classList.add('active');
  sonar.classList.add('active');
  statusText.textContent = 'Meminta izin mikrofon...';

  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    statusText.textContent = 'Merekam ucapan Anda selama 1.5 detik...';
    
    const actx = new (window.AudioContext || window.webkitAudioContext)();
    const source = actx.createMediaStreamSource(stream);
    const processor = actx.createScriptProcessor(4096, 1, 1);
    
    let recordedBuffers = [];
    let recordingLength = 0;
    let isRecording = true;
    
    source.connect(processor);
    processor.connect(actx.destination);
    
    processor.onaudioprocess = (e) => {
      if (!isRecording) return;
      const input = e.inputBuffer.getChannelData(0);
      recordedBuffers.push(new Float32Array(input));
      recordingLength += input.length;
    };
    
    setTimeout(async () => {
      isRecording = false;
      stream.getTracks().forEach(track => track.stop());
      processor.disconnect();
      if (actx.state !== 'closed') actx.close();
      statusText.textContent = 'Menganalisis audio...';
      
      const result = new Float32Array(recordingLength);
      let offset = 0;
      for (let i = 0; i < recordedBuffers.length; i++) {
        result.set(recordedBuffers[i], offset);
        offset += recordedBuffers[i].length;
      }
      
      const wavBlob = encodeWAV(result, actx.sampleRate);
      const formData = new FormData();
      formData.append("file", wavBlob, "recording.wav");
      
      try {
        const response = await fetch(`${settings.aiApi}/api/classify-audio`, {
          method: 'POST',
          body: formData
        });
        
        if (response.ok) {
          const data = await response.json();
          document.getElementById('ai-class').textContent = data.predicted_class.toUpperCase();
          document.getElementById('ai-conf').textContent = `${(data.confidence * 100).toFixed(0)}%`;
          statusText.textContent = `Deteksi selesai! Wilayah: ${data.region}`;

          if (data.song) {
            const matchedSong = songs.find(s => s.id === data.song);
            if (matchedSong) {
              setTimeout(() => {
                navigateTo('page-pustaka');
                playSong(matchedSong.id, matchedSong.notes);
              }, 1500);
            }
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
    }, 1500);
    
  } catch (e) {
    statusText.textContent = 'Gagal mengakses mikrofon browser.';
    micBtn.disabled = false;
    micBtn.classList.remove('active');
    sonar.classList.remove('active');
  }
}
// Helper: Stop all active timers/sockets when exiting a page
function stopAllPlaybacks() {
  if (activeSongInterval) {
    clearInterval(activeSongInterval);
    activeSongInterval = null;
  }

  const playButtons = document.querySelectorAll('.song-play-btn');
  playButtons.forEach(btn => {
    btn.classList.remove('playing');
    btn.innerHTML = '<i class="fa-solid fa-play"></i>';
  });

  if (micStream) {
    micStream.getTracks().forEach(track => track.stop());
    micStream = null;
  }
  if (scriptProcessor) {
    scriptProcessor.disconnect();
    scriptProcessor = null;
  }
  if (audioContext && audioContext.state !== 'closed') {
    audioContext.close();
    audioContext = null;
  }

  if (repeaterSocket) {
    try { repeaterSocket.close(); } catch (_) {}
    repeaterSocket = null;
  }
  isRepeaterListening = false;  
  const micBtn = document.getElementById('mic-repeater-btn');
  if (micBtn) micBtn.classList.remove('active');
  
  const sonar = document.querySelector('.sonar-wave.wave-green');
  if (sonar) sonar.classList.remove('active');
  
  const repStatus = document.getElementById('repeater-status');
  if (repStatus) repStatus.textContent = 'Ketuk mikrofon untuk mendengarkan nada';

  // Stop any custom song playing on Python backend
  fetch(`${settings.localApi}/api/arduino/stop_song`).catch(() => {});
}
