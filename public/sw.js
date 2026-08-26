const CACHE_NAME = 'rima-pwa-v2';
const urlsToCache = [
  './',
  './index.html',
  './style.css',
  './app.js',
  './logo.png'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => {
        return cache.addAll(urlsToCache);
      })
  );
});

self.addEventListener('fetch', event => {
  if (!event.request.url.startsWith('http')) return;
  
  // Bypass cache untuk panggilan API dan WebSocket ke backend Python
  if (event.request.url.includes('/api/') || event.request.url.includes('/ws/')) {
    return; // Biarkan browser menghandle request secara native (network only)
  }
  
  // Cache First Strategy untuk UI (Cepat dimuat di Kiosk)
  event.respondWith(
    caches.match(event.request)
      .then(cachedResponse => {
        if (cachedResponse) {
          return cachedResponse;
        }
        
        return fetch(event.request).then(response => {
          if (!response || response.status !== 200 || response.type !== 'basic') {
            return response;
          }
          const responseClone = response.clone();
          caches.open(CACHE_NAME).then(cache => {
            cache.put(event.request, responseClone);
          });
          return response;
        });
      })
  );
});

self.addEventListener('activate', event => {
  const cacheWhitelist = [CACHE_NAME];
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(cacheName => {
          if (cacheWhitelist.indexOf(cacheName) === -1) {
            return caches.delete(cacheName);
          }
        })
      );
    })
  );
});
