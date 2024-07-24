class ConnectServices {
    constructor() {}

    async verifyToken() {
        console.log("Verifying token");
        const jwtToken = localStorage.getItem('jwtToken');
        console.log("Token:", jwtToken);
        if (!jwtToken) {
            return false;
        }
    
        try {
            const response = await fetch('http://127.0.0.1:8080/api/validateToken', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + jwtToken
                }
            });

            console.log("Token verification response status:", response.status);
    
            if (response.status === 200) {
                const data = await response.json();
                
                console.log("Token verification response:", data);
                console.log("Token verification response (type):", typeof data);
                console.log("Token verification status:", data.status);
                return data.status === 'valid';
            } else {
                return false;
            }
        } catch (error) {
            console.error('Error verifying token:', error);
            return false;
        }
    }

    async login(username, password) {
        console.log("Attempting login");
        try {
            const response = await fetch('http://127.0.0.1:8080/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            const jsonResponse = await response.json(); // Lire le corps de la réponse une seule fois
            console.log("Login successful");
            console.log("Login response JSON:", jsonResponse);
            return jsonResponse;
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
        }
    }
}

export { ConnectServices };
