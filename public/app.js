// ====================================================================
// R.I.M.A SPA WEB CONTROLLER APPLICATION LOGIC
// ====================================================================

// 1. Connection Configurations (Load from localStorage or default)
let settings = {
  port1: localStorage.getItem('rima_port_1') || 'COM10',
  port2: localStorage.getItem('rima_port_2') || 'COM11',
  port3: localStorage.getItem('rima_port_3') || 'COM12',
  hostApi: localStorage.getItem('rima_host_api') || 'http://localhost:8000'
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

let activeSongInterval = null;
let repeaterSocket = null;
let isRepeaterListening = false;
let keyIntervals = new Map();

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

// 3. Application Startup
document.addEventListener('DOMContentLoaded', () => {
  // Set initial settings values to modal inputs
  document.getElementById('input-com-port-1').value = settings.port1;
  document.getElementById('input-com-port-2').value = "Terintegrasi dengan Angklung 1";
  document.getElementById('input-com-port-3').value = settings.port3;
  document.getElementById('input-host-api').value = settings.hostApi;

  // Initialize view and run background connection checks
  loadSongsFromBackend();
  checkConnections();
  setInterval(checkConnections, 6000); // Check connections every 6 seconds

  // Attach Piano Keys Interaction listeners
  const keys = document.querySelectorAll('.key');
  keys.forEach(key => {
    // Mouse interaction
    key.addEventListener('mousedown', (e) => {
      e.preventDefault();
      startKeyTrigger(key);
    });
    key.addEventListener('mouseup', () => stopKeyTrigger(key));
    key.addEventListener('mouseleave', () => stopKeyTrigger(key));
    
    // Touch interaction
    key.addEventListener('touchstart', (e) => {
      e.preventDefault();
      startKeyTrigger(key);
    });
    key.addEventListener('touchend', () => stopKeyTrigger(key));
    key.addEventListener('touchcancel', () => stopKeyTrigger(key));
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
  const host = settings.hostApi;

  // Check python FastAPI status
  let isApiOnline = false;
  try {
    const response = await fetch(`${host}/api/health`, { method: 'GET' });
    if (response.ok) isApiOnline = true;
  } catch (_) {}

  // Check serial com status on python for all 3 devices
  let statuses = { angklung1: 'offline', angklung2: 'offline', angklung3: 'offline' };
  if (isApiOnline) {
    try {
      // Sync configurations to python backend
      await fetch(`${host}/api/config-arduino`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          port1: settings.port1,
          port2: settings.port2,
          port3: settings.port3
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
  }

  // Update Status UI badges in Modal
  updateBadge('modal-api-status', isApiOnline);
  updateBadge('modal-serial-status-1', statuses.angklung1 === 'online');
  updateBadge('modal-serial-status-2', statuses.angklung2 === 'online');
  updateBadge('modal-serial-status-3', statuses.angklung3 === 'online');
}

function updateBadge(id, isOnline) {
  const badge = document.getElementById(id);
  if (badge) {
    if (isOnline) {
      badge.textContent = 'Connected';
      badge.className = 'badge badge-green';
    } else {
      badge.textContent = 'Offline';
      badge.className = 'badge badge-red';
    }
  }
}

// Settings Overlay Handlers
function toggleSettingsModal() {
  const modal = document.getElementById('settings-modal');
  modal.classList.toggle('active');
}

function saveConnectionSettings() {
  const p1 = document.getElementById('input-com-port-1').value.trim();
  const p3 = document.getElementById('input-com-port-3').value.trim();
  const hostVal = document.getElementById('input-host-api').value.trim();

  settings.port1 = p1;
  settings.port2 = p1; // Share same port with Angklung 1
  settings.port3 = p3;
  settings.hostApi = hostVal;

  localStorage.setItem('rima_port_1', p1);
  localStorage.setItem('rima_port_2', p1);
  localStorage.setItem('rima_port_3', p3);
  localStorage.setItem('rima_host_api', hostVal);

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
  fetch(`${settings.hostApi}/api/arduino/play?note=${noteNum}&angklung_id=${angklungId}`).catch(() => {});

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

// 7. Chord Triggering (Fm, F#m, Gm, G#m)
function playChord(chordName) {
  let notesParam = "";
  
  // Custom note mappings mapped to note indices (1 to 16)
  switch (chordName) {
    case 'Fm': notesParam = "10,13,1"; break;   // f2, g#2, c3
    case 'F#m': notesParam = "11,14,2"; break;  // f#2, a2, c#3
    case 'Gm': notesParam = "12,15,3"; break;   // g2, a#2, d3
    case 'G#m': notesParam = "13,16,4"; break;  // g#2, b2, d#3
  }

  document.getElementById('active-note-display').textContent = chordName;

  // Send request to backend targeting Angklung 2 (Medium register)
  fetch(`${settings.hostApi}/api/arduino/play_chord?notes=${notesParam}&angklung_id=2`).catch(() => {});

  // Visually animate keys belonging to the chord on Angklung 2 and trigger sound
  const noteIndices = notesParam.split(',');
  noteIndices.forEach(idx => {
    const key = document.querySelector(`.key[data-note="${idx}"][data-angklung="2"]`);
    if (key) {
      key.classList.add('active');
      
      const noteNum = parseInt(idx, 10);
      const freqMap = NOTE_FREQUENCIES[2];
      if (freqMap && freqMap[noteNum]) {
        playClientSynthSound(freqMap[noteNum]);
      }
      
      setTimeout(() => key.classList.remove('active'), 350);
    }
  });
}

// 8. Pustaka Lagu Section
function loadSongsList(filter = 'all') {
  const container = document.getElementById('songs-container');
  container.innerHTML = '';

  const filtered = filter === 'all' ? songs : songs.filter(s => s.category === filter);

  filtered.forEach(song => {
    // Escape single quotes for HTML onClick
    const cleanId = song.id.replace(/'/g, "\\'");
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
      <button class="song-play-btn" id="btn-play-${cleanId}" onclick="playSong('${cleanId}')">
        <i class="fa-solid fa-play"></i>
      </button>
    `;
    container.appendChild(item);
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
  const btnId = `btn-play-${songId}`;
  const playBtn = document.getElementById(btnId);
  
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
    const response = await fetch(`${settings.hostApi}/api/arduino/play_song_file`, {
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
      const response = await fetch(`${settings.hostApi}/api/arduino/play_song_file`, {
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
  fetch(`${settings.hostApi}/api/arduino/stop_song`)
    .then(() => {
      console.log("[PLAYER] Menghentikan pemutaran lagu di server.");
    })
    .catch(() => {});
}

// 9. Repeater Section (Pitch Tuning via Websocket)
function toggleRepeaterListening() {
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
  statusText.textContent = 'Mendengarkan nada... Dekatkan angklung ke mikrofon!';

  // Connect to FastAPI WebSocket endpoint
  const wsHost = settings.hostApi.replace('http://', 'ws://');
  try {
    repeaterSocket = new WebSocket(`${wsHost}/ws/pitch`);
    
    repeaterSocket.onmessage = (event) => {
      const data = JSON.parse(event.data);
      if (data.frequency > 0) {
        document.getElementById('repeater-note').textContent = data.note;
        document.getElementById('repeater-freq').textContent = `${data.frequency.toFixed(1)} Hz`;
        
        // Match frequency to closest note number and trigger flash
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
async function triggerLanguageClassification() {
  const micBtn = document.getElementById('mic-bahasa-btn');
  const sonar = document.getElementById('ai-waves');
  const statusText = document.getElementById('ai-status');

  micBtn.disabled = true;
  micBtn.classList.add('active');
  sonar.classList.add('active');
  statusText.textContent = 'Merekam ucapan Anda selama 1.5 detik...';

  try {
    const response = await fetch(`${settings.hostApi}/api/record-and-classify`, { method: 'POST' });
    if (response.ok) {
      const data = await response.json();
      
      document.getElementById('ai-class').textContent = data.predicted_class.toUpperCase();
      document.getElementById('ai-conf').textContent = `${(data.confidence * 100).toFixed(0)}%`;
      statusText.textContent = `Deteksi selesai! Wilayah: ${data.region}`;

      // Automatically play corresponding regional song
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
}

// Helper: Stop all active timers/sockets when exiting a page
function stopAllPlaybacks() {
  // Stop Song Interval
  if (activeSongInterval) {
    clearInterval(activeSongInterval);
    activeSongInterval = null;
  }

  // Stop Play buttons class
  const playButtons = document.querySelectorAll('.song-play-btn');
  playButtons.forEach(btn => {
    btn.classList.remove('playing');
    btn.innerHTML = '<i class="fa-solid fa-play"></i>';
  });

  // Stop Repeater WebSocket
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
  fetch(`${settings.hostApi}/api/arduino/stop_song`).catch(() => {});
}
