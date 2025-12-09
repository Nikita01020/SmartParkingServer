from flask import Flask, jsonify, request
from flask_cors import CORS
import json
import os

app = Flask(__name__)
CORS(app)

# ---------------------------------------
#   Настройки
# ---------------------------------------

LOG_FILE = "parking_actions.log"

# Парковочные места в памяти
# Индексы 0..N-1, но id = index + 1
parking_places = [
    {"status": "free"},
    {"status": "busy"},
    {"status": "free"},
    {"status": "busy"},
    {"status": "free"},
]

# ---------------------------------------
#   Функция логирования
# ---------------------------------------

def log_action(action: str):
    """Корректная запись в файл с использованием with open()."""
    with open(LOG_FILE, "a", encoding="utf-8") as log:
        log.write(action + "\n")

# ---------------------------------------
#   Маршруты сервера
# ---------------------------------------

@app.get("/places")
def get_places():
    """Возвращает список парковочных мест с ID начиная с 1."""
    output = {
        "places": [
            {"id": idx + 1, "status": place["status"]}
            for idx, place in enumerate(parking_places)
        ]
    }
    return jsonify(output)

@app.post("/toggle")
def toggle_place():
    """Переключает состояние места (free/busy)."""
    data = request.json

    if "id" not in data:
        return jsonify({"error": "id required"}), 400

    place_id = data["id"]
    index = place_id - 1  # переводим в индекс списка

    if index < 0 or index >= len(parking_places):
        return jsonify({"error": "invalid id"}), 400

    # Переключение статуса
    old_status = parking_places[index]["status"]
    new_status = "busy" if old_status == "free" else "free"
    parking_places[index]["status"] = new_status

    # Логируем корректно
    log_action(f"[TOGGLE] Place {place_id} changed {old_status} → {new_status}")

    return jsonify({"id": place_id, "status": new_status})

# ---------------------------------------
#   Запуск сервера
# ---------------------------------------

if __name__ == "__main__":
    print("🚗 SmartParkingServer started on http://localhost:8000")
    app.run(host="0.0.0.0", port=8000)
