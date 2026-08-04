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
let keyIntervals = new Map();
let chordIntervals = new Map();

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
    document.getElementById('notes-indicator-container').style.opacity = '1';

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

let midiSocket = null;

function setKeyProgrammaticState(noteNum, angklungId, isDown) {
  const key = document.querySelector(`.key[data-note="${noteNum}"][data-angklung="${angklungId}"]`);
  if (key) {
    if (isDown) {
      key.classList.add('active');
      document.getElementById('active-note-display').textContent = key.getAttribute('data-label').toUpperCase();
      document.getElementById('notes-indicator-container').style.opacity = '1';
      
      const freqMap = NOTE_FREQUENCIES[angklungId];
      if (freqMap && freqMap[noteNum]) {
        playClientSynthSound(freqMap[noteNum]);
      }
    } else {
      key.classList.remove('active');
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
    try {
      const data = JSON.parse(event.data);
      if (data.note && data.angklung) {
        setKeyProgrammaticState(data.note, data.angklung, data.action === "down");
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
  document.getElementById('input-com-port-1').value = settings.port1;
  document.getElementById('input-com-port-2').value = "Terintegrasi dengan Angklung 1";
  document.getElementById('input-com-port-3').value = settings.port3;
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
function navigateTo(pageId) {
  // Clear any running song playbacks or socket connections when switching pages
  stopAllPlaybacks();
  if (pageId !== 'page-bahasa') {
    setBahasaMode(1);
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

// Settings Overlay Handlers
async function scanMidiDevices() {
  const host = settings.hostApi;
  const select = document.getElementById('select-midi-device');
  if (!select) return;

  try {
    const response = await fetch(`${host}/api/midi/devices`);
    if (response.ok) {
      const devices = await response.json();
      const currentVal = select.value;
      
      select.innerHTML = '<option value="">-- Scan/Pilih Keyboard MIDI --</option>';
      
      devices.forEach(device => {
        const option = document.createElement('option');
        option.value = device.id;
        option.textContent = `${device.name} (${device.interface})`;
        select.appendChild(option);
      });
      
      if (devices.some(d => d.id.toString() === currentVal)) {
        select.value = currentVal;
      }
    }
  } catch (err) {
    console.error("Gagal melakukan scan perangkat MIDI:", err);
  }
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
    
    // Check connected midi status
    const host = settings.hostApi;
    try {
      const response = await fetch(`${host}/api/midi/status`);
      if (response.ok) {
        const data = await response.json();
        const selectMidi = document.getElementById('select-midi-device');
        if (selectMidi && data.active && data.device_id !== null) {
          selectMidi.value = data.device_id;
        }
      }
    } catch (_) {}
  }
  modal.classList.toggle('active');
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

  // Connect or disconnect MIDI device
  if (selectMidi && selectMidi.value !== "") {
    const deviceId = parseInt(selectMidi.value, 10);
    try {
      await fetch(`${hostVal}/api/midi/connect`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ device_id: deviceId })
      });
      localStorage.setItem('rima_midi_device_id', deviceId);
    } catch (e) {
      console.error("Gagal menyambung MIDI:", e);
    }
  } else {
    try {
      await fetch(`${hostVal}/api/midi/disconnect`, { method: 'POST' });
      localStorage.removeItem('rima_midi_device_id');
    } catch (_) {}
  }

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
  document.getElementById('notes-indicator-container').style.opacity = '1';

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
  const key = document.querySelector(`.key[data-note="${noteNum}"][data-angklung="${angklungId}"]`);
  if (key) {
    key.classList.add('active');
    document.getElementById('active-note-display').textContent = key.getAttribute('data-label').toUpperCase();
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
    
    // Update Album Art placeholder info
    const titleEl = document.getElementById('current-album-title');
    const descEl = document.getElementById('current-album-desc');
    if (titleEl && descEl) {
      const title = songRow.querySelector('h4').innerText;
      const desc = songRow.querySelector('p').innerText;
      titleEl.innerText = title;
      descEl.innerText = desc;
    }
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
  const sonar = document.querySelector('.sonar-wave.wave-green');
  const statusText = document.getElementById('repeater-status');

  if (repeaterState === 'playing') {
    // Stop playback if playing
    stopAllPlaybacks();
    return;
  }

  if (repeaterState === 'recording') {
    // Stop recording and start playback
    repeaterState = 'idle';
    if (repeaterSocket) {
      repeaterSocket.close();
      repeaterSocket = null;
    }
    
    micBtn.classList.remove('active');
    sonar.classList.remove('active');
    statusText.textContent = 'Memproses & memainkan urutan nada...';
    
    // Finalize the active note cleanly if exists (ignore short trailing release drops)
    if (currentRecDuration >= 100 && currentRecNote !== null) {
      recordedSequence.push({ note: currentRecNote, duration: Math.round(currentRecDuration) });
    }
    currentRecNote = null;
    currentRecDuration = 0;
    
    playRepeaterSequence(recordedSequence, statusText, micBtn, sonar);
    return;
  }

  // Start recording
  repeaterState = 'recording';
  recordedSequence = [];
  currentRecNote = null;
  currentRecDuration = 0;
  let noteHistory = []; // Buffer 5 frame untuk menghaluskan nada
  
  micBtn.classList.add('active');
  sonar.classList.add('active');
  statusText.textContent = 'Merekam nada vokal... Tekan lagi untuk putar ulang!';

  // Clear sequence UI
  const sequenceContainer = document.getElementById('repeater-note-sequence');
  if (sequenceContainer) sequenceContainer.innerHTML = '';
  window.lastRepeaterNote = null;

  // Connect to FastAPI WebSocket endpoint with high-precision timestamp tracking
  const wsHost = settings.hostApi.replace('http://', 'ws://');
  let lastFrameTime = performance.now();

  try {
    repeaterSocket = new WebSocket(`${wsHost}/ws/pitch`);
    
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
      if (noteHistory.length > 2) noteHistory.shift();
      
      let activeNote = rawNote;
      if (noteHistory.length === 2 && noteHistory[0] === noteHistory[1]) {
        activeNote = noteHistory[0];
      }
      
      // Accumulate exact measured duration per note/silence segment using high-res performance timestamps
      if (activeNote === currentRecNote) {
        currentRecDuration += measuredDurMs;
      } else {
        if (currentRecDuration > 0) {
          recordedSequence.push({ 
            note: currentRecNote, 
            duration: Math.round(currentRecDuration),
            centsDev: centsDev,
            confidence: confidence 
          });
        }
        currentRecNote = activeNote;
        currentRecDuration = measuredDurMs;
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

  // Step 1: Merge consecutive identical or near-identical notes (<= 1 semitone difference)
  let merged = [];
  for (let item of rawSequence) {
    let note = item.note || null;
    if (merged.length > 0) {
      const last = merged[merged.length - 1];
      if (last.note === note) {
        last.duration += item.duration;
        continue;
      }
      // If adjacent notes are identical or micro-variations of same pitch, merge into one continuous note!
      if (last.note !== null && note !== null) {
        const p1 = parsePitchNote(last.note);
        const p2 = parsePitchNote(note);
        if (p1 && p2 && Math.abs(p1.midi - p2.midi) <= 1 && item.duration < 150) {
          last.duration += item.duration;
          continue;
        }
      }
    }
    merged.push({ note: note, duration: item.duration });
  }

  // Step 2: Micro-Gap Filler (Fill tiny silence gaps < 200ms between notes to create smooth Legato)
  let legato = [];
  for (let i = 0; i < merged.length; i++) {
    const item = merged[i];
    const prev = i > 0 ? merged[i - 1] : null;
    const next = i < merged.length - 1 ? merged[i + 1] : null;

    if (item.note === null && item.duration < 200 && prev && next && prev.note !== null && next.note !== null) {
      // Micro-silence gap -> Extend previous note over the gap so notes connect smoothly in Legato!
      prev.duration += item.duration;
      continue;
    }
    legato.push(item);
  }

  // Step 3: Re-merge consecutive identical notes
  let reMerged = [];
  for (let item of legato) {
    if (reMerged.length > 0 && reMerged[reMerged.length - 1].note === item.note) {
      reMerged[reMerged.length - 1].duration += item.duration;
    } else {
      reMerged.push({ note: item.note, duration: item.duration });
    }
  }

  // Step 4: Preserve exact recorded note duration matching singer's actual timing 1-to-1
  let filtered = [];
  for (let i = 0; i < reMerged.length; i++) {
    const item = reMerged[i];
    const prev = i > 0 ? reMerged[i - 1] : null;
    const next = i < reMerged.length - 1 ? reMerged[i + 1] : null;

    if (item.note === null) {
      filtered.push(item);
      continue;
    }

    // Preserve exact duration down to 100ms (so short 0.1s notes stay short and long 1.5s notes stay long!)
    if (item.duration < 90) {
      if (prev && prev.note !== null) prev.duration += item.duration;
      else if (next && next.note !== null) next.duration += item.duration;
    } else {
      filtered.push(item);
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
  "a_minor":     [9, 11, 0, 2, 4, 5, 7], // A, B, C, D, E, F, G
  "e_minor":     [4, 6, 7, 9, 11, 0, 2], // E, F#, G, A, B, C, D
  "pelog_sunda": [0, 1, 5, 7, 8],        // C, C#, F, G, G#
  "slendro":     [0, 2, 5, 7, 9]         // C, D, F, G, A
};

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

  // Find nearest note in scale (closest MIDI distance)
  let bestMidi = parsed.midi;
  let minDiff = 999;

  for (let delta = -2; delta <= 2; delta++) {
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

  // Direct Monotonic Clamp to Physical Angklung MIDI Range [52 = E3 to 96 = C7]
  bestMidi = Math.max(52, Math.min(96, bestMidi));
  return midiToPitchNameString(bestMidi, parsed.isBass);
}

function autoTuneHarmonicSequence(sequence) {
  if (!sequence || sequence.length === 0) return [];

  const targetScale = detectBestHarmonicScale(sequence);

  let tunedSequence = sequence.map(item => {
    if (!item.note) return { note: null, duration: item.duration };
    const tunedNote = snapNoteToHarmonicScale(item.note, targetScale);
    return { note: tunedNote, duration: item.duration };
  });

  let merged = [];
  for (let item of tunedSequence) {
    if (merged.length > 0 && merged[merged.length - 1].note === item.note) {
      merged[merged.length - 1].duration += item.duration;
    } else {
      merged.push({ note: item.note, duration: item.duration });
    }
  }

  return merged;
}

// 100% Diatonic Scale Auto-Tune & Harmonics Quantizer (Eliminates all fals chromatic accidentals)
function applyTieredAutotune(sequence, mode = 'soft') {
  if (!sequence || sequence.length === 0) return sequence;

  // Detect dominant harmonic key scale of the recorded song melody
  const targetScale = detectBestHarmonicScale(sequence);

  let tunedSequence = sequence.map(item => {
    if (!item.note) return item;
    const parsed = parsePitchNote(item.note);
    if (!parsed) return item;

    // 100% Quantize note to clean harmonic scale to eliminate all off-key ("fals") accidentals
    const snapped = snapNoteToHarmonicScale(item.note, targetScale);
    return { 
      note: snapped, 
      duration: item.duration, 
      centsDev: item.centsDev,
      scaleDegree: item.scaleDegree 
    };
  });

  // Re-merge adjacent notes that snapped to the same pitch
  let merged = [];
  for (let item of tunedSequence) {
    if (merged.length > 0 && merged[merged.length - 1].note === item.note) {
      merged[merged.length - 1].duration += item.duration;
    } else {
      merged.push(item);
    }
  }

  return merged;
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

// Render transcribed vocal melody note chips with Scale Degree (Do-Re-Mi) badges
function renderRepeaterNoteChips(sequence) {
  const sequenceContainer = document.getElementById('repeater-note-sequence');
  if (!sequenceContainer) return;
  sequenceContainer.innerHTML = '';

  const chipsWrapper = document.createElement('div');
  chipsWrapper.style.display = 'flex';
  chipsWrapper.style.flexWrap = 'wrap';
  chipsWrapper.style.gap = '8px';
  chipsWrapper.style.width = '100%';

  sequence.forEach((item) => {
    if (!item.note) return;
    
    // Compute scale degree if missing
    let scaleBadge = item.scaleDegree || '';
    if (!scaleBadge) {
      const parsed = parsePitchNote(item.note);
      if (parsed) {
        const doremiMap = ['1 (Do)', '1/ (Do#)', '2 (Re)', '2/ (Re#)', '3 (Mi)', '4 (Fa)', '4/ (Fa#)', '5 (Sol)', '5/ (Sol#)', '6 (La)', '6/ (La#)', '7 (Si)'];
        scaleBadge = doremiMap[parsed.pitchClass] || '';
      }
    }

    const chip = document.createElement('div');
    chip.style.background = '#E8F5E9';
    chip.style.border = '1px solid #81C784';
    chip.style.borderRadius = '20px';
    chip.style.padding = '8px 16px';
    chip.style.fontWeight = '800';
    chip.style.color = '#1B5E20';
    chip.style.boxShadow = '0 2px 8px rgba(76, 175, 80, 0.15)';
    chip.style.display = 'flex';
    chip.style.alignItems = 'center';
    chip.style.gap = '6px';
    chip.innerHTML = `<span>🎵 ${item.note}</span> <span style="font-size: 11px; background: #C8E6C9; padding: 2px 8px; border-radius: 10px; color: #2E7D32;">${scaleBadge}</span> <span style="font-size: 11px; opacity: 0.85; font-weight: 600;">(${(item.duration / 1000).toFixed(1)}s)</span>`;
    chipsWrapper.appendChild(chip);
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

  renderRepeaterNoteChips(sequence);

  if (sequence.length === 0) {
    statusText.textContent = 'Tidak ada nada stabil yang terdeteksi. Coba nyanyikan nada lebih jelas!';
    repeaterState = 'idle';
    micBtn.classList.remove('active');
    micBtn.classList.remove('mic-playing');
    sonar.classList.remove('active');
    return;
  }

  repeaterState = 'playing';
  micBtn.classList.add('active');
  micBtn.classList.add('mic-playing');
  sonar.classList.add('active');
  statusText.textContent = 'Memainkan melodi persis hasil rekaman vokal...';
  
  for (let item of sequence) {
    if (repeaterState !== 'playing') break;
    
    if (item.note !== null) {
      let hw = mapPitchNameToNoteNumber(item.note);
      if (hw) {
        document.getElementById('repeater-note').textContent = item.note;
        
        // Highlight visual key and sustain Web Audio synth for EXACT item.duration
        highlightKeyProgrammatic(hw.note, hw.angklung, true, item.duration);
        
        // Sustain physical hardware angklung shaking throughout item.duration
        playChordForNoteNumSustained(hw.note, hw.angklung, item.duration);
        
        // Hold playback execution for exact duration of note
        await new Promise(r => setTimeout(r, item.duration));
      } else {
        await new Promise(r => setTimeout(r, item.duration));
      }
    } else {
      // Rest/Silence pause between notes
      document.getElementById('repeater-note').textContent = '---';
      await new Promise(r => setTimeout(r, item.duration));
    }
  }
  
  if (repeaterState === 'playing') {
    repeaterState = 'idle';
    micBtn.classList.remove('active');
    micBtn.classList.remove('mic-playing');
    sonar.classList.remove('active');
    statusText.textContent = 'Ketuk mikrofon untuk merekam nada';
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
window.handleGlobalSearch = handleGlobalSearch;
window.nextMenu = nextMenu;
window.prevMenu = prevMenu;
window.playBahasaSong = playBahasaSong;
window.resetBahasaPage = resetBahasaPage;
window.setBahasaMode = setBahasaMode;