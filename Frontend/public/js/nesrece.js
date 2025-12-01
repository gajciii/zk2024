const API_BASE_URL = 'http://localhost:8080/api/v1';

async function loadNesrece() {
    try {
        const response = await fetch(`${API_BASE_URL}/nesrece`);
        const nesrece = await response.json();
        
        const tbody = document.getElementById('nesreceTable');
        
        if (nesrece.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Ni podatkov</td></tr>';
            return;
        }
        
        tbody.innerHTML = nesrece.map(n => `
            <tr>
                <td>${n.id}</td>
                <td>${n.opis || 'N/A'}</td>
                <td>${n.lokacija || 'N/A'}</td>
                <td>${n.datum || 'N/A'}</td>
                <td>${n.status || 'N/A'}</td>
            </tr>
        `).join('');
        
    } catch (error) {
        document.getElementById('errorMessage').textContent = 'Napaka pri pridobivanju nesreč';
        document.getElementById('errorMessage').style.display = 'block';
        document.getElementById('nesreceTable').innerHTML = '<tr><td colspan="5" style="text-align: center;">Napaka pri nalaganju</td></tr>';
    }
}

loadNesrece();
