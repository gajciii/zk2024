const API_BASE_URL = 'http://localhost:8080/api/v1';

async function loadDonacije() {
    try {
        const response = await fetch(`${API_BASE_URL}/uporabniki/donacije`);
        const donacije = await response.json();
        
        const tbody = document.getElementById('donacijeTable');
        
        if (donacije.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Ni podatkov</td></tr>';
            return;
        }
        
        tbody.innerHTML = donacije.map(d => `
            <tr>
                <td>${d.id}</td>
                <td>${d.znesek} €</td>
                <td>${d.datumDonacije || 'N/A'}</td>
                <td>${d.nacinPlacila || 'N/A'}</td>
                <td>${d.status || 'N/A'}</td>
            </tr>
        `).join('');
        
    } catch (error) {
        document.getElementById('errorMessage').textContent = 'Napaka pri pridobivanju donacij';
        document.getElementById('errorMessage').style.display = 'block';
        document.getElementById('donacijeTable').innerHTML = '<tr><td colspan="5" style="text-align: center;">Napaka pri nalaganju</td></tr>';
    }
}

document.getElementById('donacijaForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const formData = {
        znesek: parseFloat(document.getElementById('znesek').value),
        nacinPlacila: document.getElementById('nacinPlacila').value,
        uporabnik: {
            id: parseInt(document.getElementById('uporabnikId').value)
        }
    };

    try {
        const response = await fetch(`${API_BASE_URL}/uporabniki/donacije`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            alert('Donacija uspešno dodana!');
            document.getElementById('donacijaForm').reset();
            loadDonacije();
        } else {
            alert('Napaka pri dodajanju donacije');
        }
    } catch (error) {
        alert('Napaka: ' + error.message);
    }
});

loadDonacije();
