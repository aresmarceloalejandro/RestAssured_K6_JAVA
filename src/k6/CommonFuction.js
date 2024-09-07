import http from 'k6/http';
import { sleep } from 'k6';

const BASE_URL = 'http://localhost:8080/api/v3';

export function performRequest(method, endpoint, body = null, headers = {}, params = {}, expectedStatusCode, retries = 3) {
    let url = `${BASE_URL}${endpoint}`;
    let res;
    let attempts = 0;

    if (Object.keys(params).length > 0) {
        const queryString = new URLSearchParams(params).toString();
        url += `?${queryString}`;
    }

    while (attempts < retries) {
        try {
            switch (method.toUpperCase()) {
                case 'GET':
                    res = http.get(url, { headers: headers });
                    break;
                case 'POST':
                    res = http.post(url, body, { headers: headers });
                    break;
                case 'PUT':
                    res = http.put(url, body, { headers: headers });
                    break;
                case 'DELETE':
                    res = http.del(url, null, { headers: headers });
                    break;
                default:
                    throw new Error(`Unsupported method: ${method}`);
            }

            if (res.status === expectedStatusCode) {
                return res;
            } else {
                console.log(`Expected status ${expectedStatusCode}, but got ${res.status}. Retrying`);
            }
        } catch (e) {
            console.log(`Request failed: ${e.message}. Retrying (${attempts + 1}/${retries})`);
        }
        attempts++;
        sleep(1);
    }

    throw new Error(`Failed after ${retries} retries`);
}

export function readFile(filePath) {
    return open(filePath, 'b');
}