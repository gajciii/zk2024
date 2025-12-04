// Mock fetch
global.fetch = jest.fn();
global.alert = jest.fn();

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

// Importamo kodo direktno z require za coverage
require('../uporabniki.js');

describe('Uporabniki Tests', () => {
    beforeEach(() => {
        fetch.mockClear();
        alert.mockClear();
        document.getElementById('uporabnikiTable').innerHTML = '';
        document.getElementById('errorMessage').style.display = 'none';
        document.getElementById('ime').value = 'Janez';
        document.getElementById('priimek').value = 'Novak';
        document.getElementById('email').value = 'janez@example.com';
        document.getElementById('telefon').value = '123456789';
    });

    // Testira uspešno nalaganje uporabnikov v tabelo
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

    // Testira prikaz sporočila, ko ni uporabnikov
    test('loadUporabniki - prazen seznam', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => []
        });

        await loadUporabniki();

        const tableBody = document.getElementById('uporabnikiTable');
        expect(tableBody.innerHTML).toContain('Ni podatkov');
    });

    // Testira obravnavo napake pri nalaganju uporabnikov
    test('loadUporabniki - napaka pri fetch', async () => {
        fetch.mockRejectedValueOnce(new Error('Network error'));

        await loadUporabniki();

        const errorMessage = document.getElementById('errorMessage');
        expect(errorMessage.textContent).toBe('Napaka pri pridobivanju uporabnikov');
        expect(errorMessage.style.display).toBe('block');
    });

    // Testira uspešno dodajanje uporabnika preko forme
    test('Dodajanje uporabnika - uspešno', async () => {
        const form = document.getElementById('uporabnikForm');

        fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => []
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => ({ id: 1, ime: 'Janez', priimek: 'Novak' })
            });

        const event = new Event('submit', { bubbles: true, cancelable: true });
        form.dispatchEvent(event);

        // Počakajmo na asinhrono operacijo
        await new Promise(resolve => setTimeout(resolve, 200));

        expect(fetch).toHaveBeenCalled();
        expect(alert).toHaveBeenCalledWith('Uporabnik uspešno dodan!');
    });
});
