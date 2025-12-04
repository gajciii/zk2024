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

    // Testira pravilno oblikovanje podatkov pri dodajanju donacije
    test('Dodajanje donacije - preverjanje formData strukture', async () => {
        const form = document.getElementById('donacijaForm');
        document.getElementById('znesek').value = '250.5';
        document.getElementById('nacinPlacila').value = 'gotovina';
        document.getElementById('uporabnikId').value = '5';

        fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => []
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => ({ id: 2, znesek: 250.5 })
            });

        const event = new Event('submit', { bubbles: true, cancelable: true });
        form.dispatchEvent(event);

        await new Promise(resolve => setTimeout(resolve, 200));

        expect(fetch).toHaveBeenCalledWith(
            'http://localhost:8080/api/v1/uporabniki/donacije',
            expect.objectContaining({
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    znesek: 250.5,
                    nacinPlacila: 'gotovina',
                    uporabnik: {
                        id: 5
                    }
                })
            })
        );
    });

    // Testira reset forme po uspešnem dodajanju donacije
    test('Dodajanje donacije - reset forme po uspešnem dodajanju', async () => {
        const form = document.getElementById('donacijaForm');
        document.getElementById('znesek').value = '500';
        document.getElementById('nacinPlacila').value = 'kartica';
        document.getElementById('uporabnikId').value = '3';

        const resetSpy = jest.spyOn(form, 'reset');

        fetch
            .mockResolvedValueOnce({
                ok: true,
                json: async () => []
            })
            .mockResolvedValueOnce({
                ok: true,
                json: async () => ({ id: 3, znesek: 500 })
            });

        const event = new Event('submit', { bubbles: true, cancelable: true });
        form.dispatchEvent(event);

        await new Promise(resolve => setTimeout(resolve, 200));

        expect(resetSpy).toHaveBeenCalled();
        resetSpy.mockRestore();
    });

});
