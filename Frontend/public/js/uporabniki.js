const API_BASE_URL = 'http://localhost:8080/api/v1';

async function loadUporabniki() {
    try {
        const response = await fetch(`${API_BASE_URL}/uporabniki`);
        const uporabniki = await response.json();
        
        const tbody = document.getElementById('uporabnikiTable');
        
        if (uporabniki.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Ni podatkov</td></tr>';
            return;
        }
        
        tbody.innerHTML = uporabniki.map(u => `
            <tr>
                <td>${u.id}</td>
                <td>${u.ime}</td>
                <td>${u.priimek}</td>
                <td>${u.email}</td>
                <td>${u.telefon}</td>
            </tr>
        `).join('');
        
    } catch (error) {
        document.getElementById('errorMessage').textContent = 'Napaka pri pridobivanju uporabnikov';
        document.getElementById('errorMessage').style.display = 'block';
        document.getElementById('uporabnikiTable').innerHTML = '<tr><td colspan="5" style="text-align: center;">Napaka pri nalaganju</td></tr>';
    }
}

document.getElementById('uporabnikForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const formData = {
        ime: document.getElementById('ime').value,
        priimek: document.getElementById('priimek').value,
        email: document.getElementById('email').value,
        telefon: document.getElementById('telefon').value
    };

    try {
        const response = await fetch(`${API_BASE_URL}/uporabniki`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            alert('Uporabnik uspešno dodan!');
            document.getElementById('uporabnikForm').reset();
            loadUporabniki();
        } else {
            alert('Napaka pri dodajanju uporabnika');
        }
    } catch (error) {
        alert('Napaka: ' + error.message);
    }
});

loadUporabniki();
