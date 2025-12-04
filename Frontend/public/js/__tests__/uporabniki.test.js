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
