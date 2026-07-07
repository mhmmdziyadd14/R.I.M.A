import http.server
import urllib.parse
import json

PORT = 5001

class MockIoTHandler(http.server.BaseHTTPRequestHandler):
    def log_message(self, format, *args):
        # Suppress standard logging to keep terminal output clean
        return

    def do_GET(self):
        parsed_url = urllib.parse.urlparse(self.path)
        path = parsed_url.path
        query = urllib.parse.parse_qs(parsed_url.query)
        
        response_data = {"status": "success"}
        
        if path == "/play":
            note = query.get("note", [""])[0]
            print(f"\n[MOCK-IOT] >>> MENGGETARKAN ANGKLUNG: NADA {note.upper()} <<<")
            response_data["message"] = f"Note {note} played"
            
        elif path == "/play_chord":
            chord = query.get("chord", [""])[0]
            print(f"\n[MOCK-IOT] >>> MENGGETARKAN ANGKLUNG AKORD: {chord.upper()} <<<")
            response_data["message"] = f"Chord {chord} played"
            
        elif path == "/play_song":
            song = query.get("song", [""])[0]
            print(f"\n[MOCK-IOT] >>> MENGGETARKAN ANGKLUNG MELODI LAGU: {song.upper()} <<<")
            response_data["message"] = f"Song {song} started"
            
        elif path == "/status":
            response_data["status"] = "online"
            response_data["device"] = "ESP32-Angklung-Mock"
            
        else:
            self.send_response(404)
            self.send_header("Content-Type", "application/json")
            self.end_headers()
            self.wfile.write(json.dumps({"status": "error", "message": "Not Found"}).encode("utf-8"))
            return

        self.send_response(200)
        self.send_header("Access-Control-Allow-Origin", "*")  # Allow CORS
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(json.dumps(response_data).encode("utf-8"))

def run_server():
    server_address = ("", PORT)
    httpd = http.server.HTTPServer(server_address, MockIoTHandler)
    print(f"\n[MOCK-IOT] Server simulasi IoT Angklung berjalan di http://localhost:{PORT}")
    print("[MOCK-IOT] Menunggu perintah getaran dari aplikasi Flutter...")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n[MOCK-IOT] Mematikan server...")
        httpd.server_close()

if __name__ == "__main__":
    run_server()
