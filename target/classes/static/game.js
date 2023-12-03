const data = {
    "id": 1,
    "ships": [],
    "gridSize": 10
}

const battleground = document.querySelector('.battleground');
const favDialog = document.getElementById("favDialog");
const dialogClose = document.querySelector(".dialogClose");
const closeModal = () => {
    favDialog.close();
};

const getShipByXandY = (x, y) => {
    return data.ships.find(ship => {
        return x >= ship.startX && x <= ship.endX && y >= ship.startY && y <= ship.endY;
    });
}

const shoot = (cell) => {
    fetch(`http://localhost:8080/games/${data.id}/shoot?x=${cell.getAttribute('data-x')}&y=${cell.getAttribute('data-y')}`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
        },
    })
        .then((response) => response.json())
        .then((res) => {
            if (res.hitsLeft === -1) {
                alert("You have LOST the game");
                window.location.reload();
            }
            if (res.hitsLeft === -2) {
                alert("You have WON the game");
                window.location.reload();
            }

            cell.classList = [];
            cell.classList.add('cell');
            cell.removeEventListener('click', shootHandler);
            if (res.hit) {
                cell.classList.add('hit')
            }
            if (!res.hit && res.ship === null) {
                cell.classList.add('miss')
            }
            if (res.ship !== null) {

                const isVertical = res.ship.startX === res.ship.endX;
                const isHorizontal = res.ship.startY === res.ship.endY;
                if (isVertical) {
                    for (let y = res.ship.startY; y <= res.ship.endY; y++) {
                        const box = document.querySelector(`div[data-x='${res.ship.startX}'][data-y='${y}']`);
                        box.classList = [];
                        box.classList.add('cell')
                        box.classList.add('sunk')
                    }
                }

                if (isHorizontal) {
                    console.log('oeee H')
                    for (let x = res.ship.startX; x <= res.ship.endX; x++) {

                        const box = document.querySelector(`div[data-x='${x}'][data-y='${res.ship.startY}']`);
                        box.classList = [];
                        box.classList.add('cell')
                        box.classList.add('sunk')
                    }
                }
            }
        })
        .catch(err => {

        })
}

const shootHandler = (event) => {
    shoot(event.target);
};

const render = () => {
    battleground.style.gridTemplateColumns = `repeat(${data.gridSize}, minmax(50px, 50px))`;
    favDialog.showModal();
    battleground.innerHTML = null;
    for (let y = 0; y < data.gridSize; y++) {
        for (let x = 0; x < data.gridSize; x++) {
            const cell = document.createElement('div');
            cell.classList.add('cell');
            cell.setAttribute('data-x', x);
            cell.setAttribute('data-y', y);
            cell.innerText = `X:${x}Y:${y}`;
            const ship = getShipByXandY(x, y);
            if (ship) {
                cell.classList.add('ship');
            }
            battleground.appendChild(cell);

            cell.addEventListener('click', shootHandler);
        }
    }
}

const handleGameStart = () => {
    fetch(`http://localhost:8080/games`,
        {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                gridSize: parseInt(document.querySelector('#grid-size').value) || 10
            })
        }
    )
        .then((response) => response.json())
        .then((res) => {
            data.ships = res.ships;
            data.id = res.id;
            data.gridSize = res.gridSize;
            render();
        })
        .catch(err => {

        })
}

document.querySelector('#start-game').addEventListener('click', handleGameStart);