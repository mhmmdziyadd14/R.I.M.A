/*     */ package com.klungbot;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Timer
/*     */ {
/*  97 */   private final TaskQueue queue = new TaskQueue();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 102 */   private final TimerThread thread = new TimerThread(this.queue);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 111 */   private final Object threadReaper = new Object() {
/*     */       protected void finalize() throws Throwable {
/* 113 */         synchronized (Timer.this.queue) {
/* 114 */           Timer.this.thread.newTasksMayBeScheduled = false;
/* 115 */           Timer.this.queue.notify();
/*     */         } 
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 123 */   private static final AtomicInteger nextSerialNumber = new AtomicInteger(0);
/*     */   private static int serialNumber() {
/* 125 */     return nextSerialNumber.getAndIncrement();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timer() {
/* 133 */     this("Timer-" + serialNumber());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timer(boolean isDaemon) {
/* 147 */     this("Timer-" + serialNumber(), isDaemon);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timer(String name) {
/* 160 */     this.thread.setName(name);
/* 161 */     this.thread.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timer(String name, boolean isDaemon) {
/* 175 */     this.thread.setName(name);
/* 176 */     this.thread.setDaemon(isDaemon);
/* 177 */     this.thread.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void schedule(TimerTask task, long delay) {
/* 192 */     if (delay < 0L)
/* 193 */       throw new IllegalArgumentException("Negative delay."); 
/* 194 */     sched(task, System.currentTimeMillis() + delay, 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void schedule(TimerTask task, Date time) {
/* 209 */     sched(task, time.getTime(), 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void schedule(TimerTask task, long delay, long period) {
/* 245 */     if (delay < 0L)
/* 246 */       throw new IllegalArgumentException("Negative delay."); 
/* 247 */     if (period <= 0L)
/* 248 */       throw new IllegalArgumentException("Non-positive period."); 
/* 249 */     sched(task, System.currentTimeMillis() + delay, -period);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void schedule(TimerTask task, Date firstTime, long period) {
/* 286 */     if (period <= 0L)
/* 287 */       throw new IllegalArgumentException("Non-positive period."); 
/* 288 */     sched(task, firstTime.getTime(), -period);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
/* 325 */     if (delay < 0L)
/* 326 */       throw new IllegalArgumentException("Negative delay."); 
/* 327 */     if (period <= 0L)
/* 328 */       throw new IllegalArgumentException("Non-positive period."); 
/* 329 */     sched(task, System.currentTimeMillis() + delay, period);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
/* 369 */     if (period <= 0L)
/* 370 */       throw new IllegalArgumentException("Non-positive period."); 
/* 371 */     sched(task, firstTime.getTime(), period);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void sched(TimerTask task, long time, long period) {
/* 388 */     if (time < 0L) {
/* 389 */       throw new IllegalArgumentException("Illegal execution time.");
/*     */     }
/*     */ 
/*     */     
/* 393 */     if (Math.abs(period) > 4611686018427387903L) {
/* 394 */       period >>= 1L;
/*     */     }
/* 396 */     synchronized (this.queue) {
/* 397 */       if (!this.thread.newTasksMayBeScheduled) {
/* 398 */         throw new IllegalStateException("Timer already cancelled.");
/*     */       }
/* 400 */       synchronized (task.lock) {
/* 401 */         if (task.state != 0) {
/* 402 */           throw new IllegalStateException("Task already scheduled or cancelled");
/*     */         }
/* 404 */         task.nextExecutionTime = time;
/* 405 */         task.period = period;
/* 406 */         task.state = 1;
/*     */       } 
/*     */       
/* 409 */       this.queue.add(task);
/* 410 */       if (this.queue.getMin() == task) {
/* 411 */         this.queue.notify();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cancel() {
/* 430 */     synchronized (this.queue) {
/* 431 */       this.thread.newTasksMayBeScheduled = false;
/* 432 */       this.queue.clear();
/* 433 */       this.queue.notify();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int purge() {
/* 458 */     int result = 0;
/*     */     
/* 460 */     synchronized (this.queue) {
/* 461 */       for (int i = this.queue.size(); i > 0; i--) {
/* 462 */         if ((this.queue.get(i)).state == 3) {
/* 463 */           this.queue.quickRemove(i);
/* 464 */           result++;
/*     */         } 
/*     */       } 
/*     */       
/* 468 */       if (result != 0) {
/* 469 */         this.queue.heapify();
/*     */       }
/*     */     } 
/* 472 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\fadli ahmad fahrezi\Downloads\KlungbotFX.jar!\com\klungbot\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */