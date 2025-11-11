from flask import Flask, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

# Эмуляция данных с парковки
@app.route('/places', methods=['GET'])
def get_places():
    data = {
        "places": [
            {"id": 1, "status": "free"},
            {"id": 2, "status": "busy"},
            {"id": 3, "status": "free"},
            {"id": 4, "status": "busy"},
            {"id": 5, "status": "free"}
        ]
    }
    return jsonify(data)

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8000)

