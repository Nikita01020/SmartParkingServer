from http.server import BaseHTTPRequestHandler, HTTPServer

class SimpleHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/plain; charset=utf-8')
        self.end_headers()
        self.wfile.write(b"SmartParking Server is running!")

if __name__ == "__main__":
    server = HTTPServer(('localhost', 8000), SimpleHandler)
    print("🚗 SmartParkingServer started on http://localhost:8000")
    server.serve_forever()
