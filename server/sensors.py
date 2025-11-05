import random
import json
import os

DATA_FILE = os.path.join(os.path.dirname(__file__), 'data', 'parking.json')

def simulate_sensor_change():
    """Случайно меняет состояние одного парковочного места"""
    with open(DATA_FILE, 'r') as f:
        data = json.load(f)

    spot = random.choice(list(data.keys()))
    data[spot] = "occupied" if data[spot] == "free" else "free"

    with open(DATA_FILE, 'w') as f:
        json.dump(data, f)

    return spot, data[spot]
