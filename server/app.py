from flask import Flask, jsonify, request
import json
import os
from datetime import datetime

app = Flask(__name__)

# Пути к данным и логам
DATA_FILE = os.path.join(os.path.dirname(__file__), 'data', 'parking.json')
LOG_FILE = os.path.join(os.path.dirname(__file__), 'logs', 'server.log')

# Создаём файлы, если их нет
os.makedirs(os.path.dirname(DATA_FILE), exist_ok=True)
os.makedirs(os.path.dirname(LOG_FILE), exist_ok=True)

if not os.path.exists(DATA_FILE):
    with open(DATA_FILE, 'w') as f:
        json.dump({"1": "free", "2": "occupied", "3": "free"}, f)

def log_action(message):
    """Записывает действия в лог"""
    with open(LOG_FILE, 'a') as log:
        log.write(f"[{datetime.now()}] {message}\n")

@app.route('/status', methods=['GET'])
def get_status():
    """Возвращает текущее состояние парковочных мест"""
    with open(DATA_FILE, 'r') as f:
        data = json.load(f)
    log_action("Получен запрос /status")
    return jsonify(data)

@app.route('/update', methods=['POST'])
def update_status():
    """Обновляет состояние парковочного места"""
    req = request.get_json()
    place_id = str(req.get('place_id'))
    status = req.get('status')

    with open(DATA_FILE, 'r') as f:
        data = json.load(f)

    if place_id in data:
        data[place_id] = status
        with open(DATA_FILE, 'w') as f:
            json.dump(data, f)
        log_action(f"Место {place_id} обновлено: {status}")
        return jsonify({"success": True, "data": data})
    else:
        log_action(f"Ошибка обновления — место {place_id} не найдено")
        return jsonify({"success": False, "error": "Place not found"}), 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

@app.route('/simulate', methods=['POST'])
def simulate_change():
    """Имитация случайного изменения состояния датчика"""
    from sensors import simulate_sensor_change
    spot, status = simulate_sensor_change()
    log_action(f"Имитация датчика: место {spot} → {status}")
    return jsonify({"changed": spot, "new_status": status})

