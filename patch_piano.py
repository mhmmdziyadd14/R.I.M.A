import re

with open("public/app.js", "r", encoding="utf-8") as f:
    content = f.read()

old_func = r"function playClientSynthSound\(frequency\) \{[\s\S]*?catch \(e\) \{\n    console\.error\(\"Gagal memutar audio Web Audio API:\", e\);\n  \}\n\}"

new_func = """function playClientSynthSound(frequency) {
  try {
    const ctx = getAudioContext();
    const now = ctx.currentTime;
    
    // Create master gain envelope for Piano
    const masterGain = ctx.createGain();
    masterGain.gain.setValueAtTime(0, now);
    masterGain.gain.linearRampToValueAtTime(0.8, now + 0.01);
    masterGain.gain.exponentialRampToValueAtTime(0.001, now + 1.5);
    masterGain.connect(ctx.destination);
    
    // Piano uses a mix of sine and triangle waves
    const osc1 = ctx.createOscillator();
    osc1.type = 'triangle';
    osc1.frequency.setValueAtTime(frequency, now);
    
    const osc2 = ctx.createOscillator();
    osc2.type = 'sine';
    osc2.frequency.setValueAtTime(frequency, now);
    
    // Filter to make it less harsh (like a piano hammer)
    const filter = ctx.createBiquadFilter();
    filter.type = 'lowpass';
    filter.frequency.setValueAtTime(frequency * 3, now);
    filter.frequency.exponentialRampToValueAtTime(frequency, now + 1.5);
    
    osc1.connect(filter);
    osc2.connect(filter);
    filter.connect(masterGain);
    
    osc1.start(now);
    osc2.start(now);
    
    osc1.stop(now + 1.6);
    osc2.stop(now + 1.6);
  } catch (e) {
    console.error("Gagal memutar audio Web Audio API:", e);
  }
}"""

content = re.sub(old_func, new_func, content)

with open("public/app.js", "w", encoding="utf-8") as f:
    f.write(content)

print("app.js patched to piano sound.")
