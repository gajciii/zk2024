// Mock fetch
global.fetch = jest.fn();
global.alert = jest.fn();

// Mock DOM
document.body.innerHTML = `
    <table>
        <tbody id="donacijeTable"></tbody>
    </table>
    <div id="errorMessage" style="display: none;"></div>
    <form id="donacijaForm">
        <input id="znesek" value="100">
        <input id="nacinPlacila" value="kartica">
        <input id="uporabnikId" value="1">
    </form>
`;

require('../donacije.js');

describe('Donacije Tests', () => {
    beforeEach(() => {
        fetch.mockClear();
        alert.mockClear();
        document.getElementById('donacijeTable').innerHTML = '';
        document.getElementById('errorMessage').style.display = 'none';
        document.getElementById('znesek').value = '100';
        document.getElementById('nacinPlacila').value = 'kartica';
        document.getElementById('uporabnikId').value = '1';
    });

    // Testira uspešno dodajanje donacije preko forme
    test('Dodajanje donacije - uspešno', async () => {
        const form = document.getElementById('donacijaForm');

        fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => []
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => ({ id: 1, znesek: 100 })
            });

        const event = new Event('submit', { bubbles: true, cancelable: true });
        form.dispatchEvent(event);

        await new Promise(resolve => setTimeout(resolve, 200));

        expect(fetch).toHaveBeenCalled();
        expect(alert).toHaveBeenCalledWith('Donacija uspešno dodana!');
    });

});
