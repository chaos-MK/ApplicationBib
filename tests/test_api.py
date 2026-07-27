import os
import pytest
import requests

BASE_URL = os.getenv("BASE_URL", "http://localhost:8080")

ENDPOINTS = [
    "/project/getall",
    "/users",
    "/users/with-dashboard",
    "/users/with-graphs",
    "/company",
    "/cohort",
]

@pytest.mark.parametrize("endpoint", ENDPOINTS)
def test_endpoint_not_dead(endpoint):
    try:
        response = requests.get(f"{BASE_URL}{endpoint}", timeout=10)
    except requests.exceptions.ConnectionError:
        pytest.fail(f"{endpoint} — connection refused, app likely crashed")
        return

    assert response.status_code < 500, (
        f"{endpoint} returned {response.status_code} — dead/broken endpoint"
    )