class ConnectServices {
    constructor() {}
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
