/*     */ package com.klungbot;
/*     */ 
/*     */ import com.klungbot.util.Options;
/*     */ import gnu.io.CommPortIdentifier;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Device
/*     */   implements Runnable
/*     */ {
/*  22 */   static ArrayList<Device> devices = new ArrayList<>(); static DeviceListener listener;
/*     */   protected String name;
/*     */   protected String devPort;
/*     */   
/*     */   static boolean isMember(ArrayList<String> ls, String s) {
/*  27 */     for (String s1 : ls) {
/*  28 */       if (s.equals(s1)) return true; 
/*     */     } 
/*  30 */     return false;
/*     */   }
/*     */   
/*     */   public static ArrayList<String> listDevicePorts() {
/*  34 */     ArrayList<String> list = new ArrayList<>();
/*  35 */     for (Device dev : devices) {
/*  36 */       if (dev.isConnected()) {
/*  37 */         list.add(dev.getPort());
/*     */       }
/*     */     } 
/*     */     
/*  41 */     Enumeration<CommPortIdentifier> en = CommPortIdentifier.getPortIdentifiers();
/*  42 */     while (en.hasMoreElements()) {
/*  43 */       CommPortIdentifier portId = en.nextElement();
/*  44 */       if (portId.getPortType() == 1 && 
/*  45 */         !isMember(list, portId.getName())) {
/*  46 */         list.add(portId.getName());
/*     */       }
/*     */     } 
/*  49 */     System.out.println("Device list " + list.toString());
/*  50 */     return list;
/*     */   }
/*     */   
/*     */   public static void loadDevices(DeviceListener aListener) {
/*  54 */     listener = aListener;
/*  55 */     String dlist = Options.get("device.enabled");
/*  56 */     if (dlist == null)
/*  57 */       return;  String[] dnames = dlist.split(":");
/*  58 */     for (String dname : dnames) {
/*  59 */       String cname = Options.get("device.class." + dname);
/*  60 */       if (cname != null)
/*     */         try {
/*  62 */           Device dev = (Device)Class.forName(cname).newInstance();
/*  63 */           dev.setName(dname);
/*  64 */           String dlatency = Options.get("device.latency");
/*  65 */           if (dlatency != null) {
/*  66 */             dev.setLatency(Integer.valueOf(dlatency).intValue());
/*     */           }
/*  68 */           dev.start();
/*  69 */           System.out.println("Starting device " + dname);
/*  70 */         } catch (Exception ex) {
/*  71 */           System.err.println("Could not start device" + dname);
/*  72 */           System.err.println("Error: " + ex.getMessage());
/*     */         }  
/*     */     } 
/*     */   }
/*     */   
/*     */   public static ArrayList<Device> getDevices() {
/*  78 */     return devices;
/*     */   }
/*     */   
/*     */   public static ArrayList<String> listDevicePlayers(String id) {
/*  82 */     return Options.getKeys("device.player." + id);
/*     */   }
/*     */   
/*     */   public static String getDefaultPort(String id) {
/*  86 */     String system = System.getProperty("os.name");
/*  87 */     System.out.println("System " + system);
/*  88 */     if (system.startsWith("Windows")) {
/*  89 */       system = "windows";
/*  90 */     } else if (system.startsWith("Mac")) {
/*  91 */       system = "mac";
/*     */     } else {
/*  93 */       system = "linux";
/*     */     } 
/*  95 */     String names = Options.get("device.port." + system + "." + id);
/*  96 */     return names;
/*     */   }
/*     */   
/*     */   public static String getActivePort() {
/* 100 */     if (devices.isEmpty()) return null; 
/* 101 */     Device dev = devices.get(0);
/* 102 */     return dev.devPort;
/*     */   }
/*     */   
/*     */   public static void setDevicePort(String port) {
/* 106 */     Device dev = devices.get(0);
/* 107 */     if (dev != null) {
/* 108 */       dev.setPort(port);
/*     */     }
/*     */   }
/*     */   
/*     */   public static long getDefaultLatency() {
/* 113 */     Device dev = devices.get(0);
/* 114 */     if (dev != null) {
/* 115 */       return dev.latency;
/*     */     }
/* 117 */     return 480L;
/*     */   }
/*     */   
/*     */   public static void setDefaultLatency(int time_ms) {
/* 121 */     for (Device dev : devices) {
/* 122 */       dev.setLatency(time_ms);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void startSendAll(long time_ms) {
/* 127 */     for (Device dev : devices)
/* 128 */       dev.startSend(time_ms); 
/*     */   }
/*     */   
/*     */   public class Event
/*     */   {
/*     */     public long tick;
/*     */     public int len;
/*     */     public byte[] data;
/*     */     
/*     */     public Event(int n) {
/* 138 */       this.data = new byte[n];
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 143 */   static int QUEUE_MAX = 128;
/* 144 */   static int DATA_MAX = 64; final ArrayBlockingQueue<Event> queue;
/*     */   final ArrayBlockingQueue<Event> pool;
/* 146 */   int latency = 480;
/* 147 */   Player[] players = null;
/*     */   
/*     */   Thread thread;
/*     */   
/*     */   public Device() {
/* 152 */     this.pool = new ArrayBlockingQueue<>(QUEUE_MAX);
/* 153 */     this.queue = new ArrayBlockingQueue<>(QUEUE_MAX);
/* 154 */     for (int i = 0; i < QUEUE_MAX; i++) {
/* 155 */       this.pool.offer(new Event(DATA_MAX));
/*     */     }
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/* 160 */     this.name = name;
/* 161 */     this.devPort = getDefaultPort(name);
/* 162 */     ArrayList<String> ls = listDevicePorts();
/* 163 */     if (ls.isEmpty())
/* 164 */       return;  for (String s : ls) {
/* 165 */       if (s.equals(this.devPort)) {
/*     */         return;
/*     */       }
/*     */     } 
/* 169 */     this.devPort = ls.get(0);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 173 */     return this.name;
/*     */   }
/*     */   
/*     */   public String getPort() {
/* 177 */     return this.devPort;
/*     */   }
/*     */   
/*     */   public void setPort(String port) {
/* 181 */     synchronized (this.queue) {
/* 182 */       this.devPort = port;
/* 183 */       this.queue.notify();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLatency(int delay) {
/* 189 */     this.latency = delay;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLatency() {
/* 194 */     return this.latency;
/*     */   }
/*     */   
/*     */   public int getMaxPlayer() {
/* 198 */     if (this.players != null) {
/* 199 */       return this.players.length;
/*     */     }
/* 201 */     return 0;
/*     */   }
/*     */   
/*     */   public Player getPlayer(int channel) {
/* 205 */     if (this.players == null) {
/* 206 */       return null;
/*     */     }
/* 208 */     if (channel >= this.players.length) {
/* 209 */       return null;
/*     */     }
/* 211 */     return this.players[channel];
/*     */   }
/*     */   
/*     */   public Player getPlayer(String name) {
/* 215 */     if (this.players == null) {
/* 216 */       return null;
/*     */     }
/* 218 */     for (Player player : this.players) {
/* 219 */       if (player.id.equals(name)) {
/* 220 */         return player;
/*     */       }
/*     */     } 
/* 223 */     return null;
/*     */   }
/*     */   
/*     */   public Event acquire() {
/*     */     Event v;
/*     */     try {
/* 229 */       v = this.pool.take();
/* 230 */     } catch (InterruptedException ex) {
/* 231 */       System.err.println("ERROR: device " + this.name + "run out of buffer");
/* 232 */       return null;
/*     */     } 
/* 234 */     return v;
/*     */   }
/*     */   
/* 237 */   static long sendTick = 0L;
/*     */   
/*     */   public void startSend(long time_ms) {
/* 240 */     sendTick = time_ms + this.latency;
/*     */   }
/*     */   
/*     */   public boolean send(Event event) {
/* 244 */     event.tick = sendTick;
/* 245 */     return this.queue.offer(event);
/*     */   }
/*     */   
/*     */   void createPlayers() {
/* 249 */     ArrayList<String> keys = listDevicePlayers(this.name);
/* 250 */     if (keys == null) {
/* 251 */       this.players = new Player[1];
/* 252 */       this.players[0] = new Instrument(this.name, this);
/*     */       return;
/*     */     } 
/* 255 */     this.players = new Player[keys.size()];
/* 256 */     int i = 0;
/* 257 */     for (String key : keys) {
/* 258 */       this.players[i] = new Instrument(Options.get(key), this, (byte)i);
/* 259 */       i++;
/*     */     } 
/*     */   }
/*     */   
/*     */   public Player[] getPlayers() {
/* 264 */     return this.players;
/*     */   }
/*     */   
/*     */   public boolean start(String devPort) {
/* 268 */     this.devPort = devPort;
/* 269 */     return start();
/*     */   }
/*     */   
/*     */   public boolean start() {
/* 273 */     if (this.thread != null) {
/* 274 */       return false;
/*     */     }
/* 276 */     devices.add(this);
/* 277 */     createPlayers();
/* 278 */     this.thread = new Thread(this);
/* 279 */     this.thread.start();
/* 280 */     return true;
/*     */   }
/*     */   
/*     */   public boolean finish() {
/* 284 */     if (this.thread == null) {
/* 285 */       return false;
/*     */     }
/* 287 */     this.thread = null;
/* 288 */     synchronized (this.queue) {
/* 289 */       this.queue.notify();
/*     */     } 
/* 291 */     devices.remove(this);
/* 292 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract boolean isConnected();
/*     */   
/*     */   abstract void write(byte[] paramArrayOfbyte) throws IOException;
/*     */   
/*     */   abstract void write(byte[] paramArrayOfbyte, int paramInt) throws IOException;
/*     */   
/*     */   private void fireConnected() {
/* 303 */     if (listener != null)
/* 304 */       listener.connected(this); 
/*     */   } abstract void flush() throws IOException;
/*     */   abstract boolean open();
/*     */   abstract boolean close();
/*     */   private void fireDisconnected() {
/* 309 */     if (listener != null) {
/* 310 */       listener.disconnected(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     label33: while (true) {
/* 319 */       fireDisconnected();
/* 320 */       this.thread.setPriority(5);
/* 321 */       long wait_time = 10L;
/* 322 */       while (!open()) {
/*     */         try {
/* 324 */           Event event; if (wait_time <= 60L) {
/* 325 */             event = this.queue.poll(wait_time, TimeUnit.SECONDS);
/* 326 */             wait_time *= 2L;
/*     */           } else {
/* 328 */             event = this.queue.take();
/*     */           } 
/* 330 */           this.pool.offer(event);
/* 331 */         } catch (Exception ex) {
/* 332 */           if (this.thread == null)
/*     */             return; 
/* 334 */         }  System.out.println("Trying to connect to " + this.devPort);
/*     */       } 
/* 336 */       System.out.println("Connected to " + this.devPort);
/* 337 */       fireConnected();
/* 338 */       this.thread.setPriority(10);
/*     */       while (true) {
/*     */         
/* 341 */         try { Event event = this.queue.take();
/* 342 */           if (event.len > 0) {
/* 343 */             write(event.data, event.len);
/* 344 */             flush();
/*     */           } 
/*     */           
/* 347 */           this.pool.offer(event); continue; }
/* 348 */         catch (InterruptedException ex)
/* 349 */         { close();
/* 350 */           if (this.thread == null)
/* 351 */             return;  continue; } catch (IOException e)
/* 352 */         { close(); }
/*     */         
/* 354 */         catch (Exception ex)
/*     */         { continue; }
/*     */         
/*     */         continue label33;
/*     */       } 
/*     */       break;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Device.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */