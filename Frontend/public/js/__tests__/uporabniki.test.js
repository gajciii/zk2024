// Mock fetch
global.fetch = jest.fn();

// Mock DOM
document.body.innerHTML = `
    <table>
        <tbody id="uporabnikiTable"></tbody>
    </table>
    <div id="errorMessage" style="display: none;"></div>
    <form id="uporabnikForm">
        <input id="ime" value="Janez">
        <input id="priimek" value="Novak">
        <input id="email" value="janez@example.com">
        <input id="telefon" value="123456789">
    </form>
`;

const loadUporabniki = async () => {
    try {
        const response = await fetch('http://localhost:8080/api/v1/uporabniki');
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
};

describe('Uporabniki Tests', () => {
    beforeEach(() => {
        fetch.mockClear();
        document.getElementById('uporabnikiTable').innerHTML = '';
        document.getElementById('errorMessage').style.display = 'none';
    });

    test('loadUporabniki - uspešno nalaganje uporabnikov', async () => {
        const mockUporabniki = [
            { id: 1, ime: 'Janez', priimek: 'Novak', email: 'janez@example.com', telefon: '123456789' },
            { id: 2, ime: 'Miha', priimek: 'Horvat', email: 'miha@example.com', telefon: '987654321' }
        ];

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockUporabniki
        });

        await loadUporabniki();

        const tableBody = document.getElementById('uporabnikiTable');
        expect(tableBody.innerHTML).toContain('Janez');
        expect(tableBody.innerHTML).toContain('Novak');
        expect(fetch).toHaveBeenCalledWith('http://localhost:8080/api/v1/uporabniki');
    });

    test('loadUporabniki - prazen seznam', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => []
        });

        await loadUporabniki();

        const tableBody = document.getElementById('uporabnikiTable');
        expect(tableBody.innerHTML).toContain('Ni podatkov');
    });

    test('loadUporabniki - napaka pri fetch', async () => {
        fetch.mockRejectedValueOnce(new Error('Network error'));

        await loadUporabniki();

        const errorMessage = document.getElementById('errorMessage');
        expect(errorMessage.textContent).toBe('Napaka pri pridobivanju uporabnikov');
        expect(errorMessage.style.display).toBe('block');
    });

    test('Dodajanje uporabnika - uspešno', async () => {
        const form = document.getElementById('uporabnikForm');
        const formData = {
            ime: 'Janez',
            priimek: 'Novak',
            email: 'janez@example.com',
            telefon: '123456789'
        };

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ id: 1, ...formData })
        });

        const event = new Event('submit');
        form.dispatchEvent(event);

        // Počakajmo na asinhrono operacijo
        await new Promise(resolve => setTimeout(resolve, 100));

        expect(fetch).toHaveBeenCalled();
    });
});

