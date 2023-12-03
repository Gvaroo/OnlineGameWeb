import { ChangeDetectorRef, Component, NgZone } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { of } from 'rxjs';
import { createRoomDTO } from 'src/app/models/Game/createRoomDTO';
import { roomDTO } from 'src/app/models/Game/roomDTO';
import { ValidationError } from 'src/app/models/Validation/ValidationError';
import { RxStompService } from 'src/app/rx-stomp.service';
import { GameService } from 'src/app/services/game.service';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.scss'],
})
export class RoomsComponent {
  rooms!: roomDTO[];

  showModal: boolean = false;

  validationError: ValidationError = {};
  loading = false;
  constructor(
    private gameService: GameService,
    private rxStompService: RxStompService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private zone: NgZone,
    private _notification: NzNotificationService
  ) {
    this.getRooms();
  }
  ngOnInit(): void {
    this.checkRoomsState();
  }

  optionList = [
    { label: '20x20', value: 20 },
    { label: '30x30', value: 30 },
    { label: '40x40', value: 40 },
    { label: '50x50', value: 50 },
    { label: '60x60', value: 60 },
    { label: '100x100', value: 100 },
  ];
  selectedValue = { label: '20x20', value: 20 };
  getRooms() {
    this.gameService.getGameRooms().subscribe((res) => {
      this.rooms = res.data;
      console.log(this.rooms);
    });
  }
  joinRoom(id: number) {
    this.gameService.joinGameRoom(id).subscribe((res) => {
      this.navigateToOponnentGameBoard(res.data.gameUid);
    });
  }
  gameBoard: string[][] = [];
  initializeGameBoard(size: number): void {
    this.gameBoard = Array.from({ length: size }, () => Array(size).fill(' '));
  }
  createRoom() {
    this.initializeGameBoard(this.selectedValue.value);
    // const gameBoardStringified = JSON.stringify(this.gameBoard);
    const createRoom: createRoomDTO = {
      size: this.selectedValue.value,
      gameBoard: this.gameBoard,
    };

    this.gameService.createGameRoom(createRoom).subscribe(
      (res) => {
        this.navigateToCreatorGameBoard(res.data.gameUid);
      },
      (err) => {
        this._notification.create(
          'error',
          'Status',
          'You cant join your own room'
        );
      }
    );
  }
  private navigateToCreatorGameBoard(roomId: string): void {
    this.router.navigate(['/ct', roomId]);
  }
  private navigateToOponnentGameBoard(roomId: string): void {
    this.router.navigate(['/op', roomId]);
  }

  openModal(): void {
    this.showModal = true;
  }
  closeModal(): void {
    this.loading = false;
    this.showModal = false;
  }
  // Method to check if the 'error' object is empty
  hasErrors(): boolean {
    return Object.keys(this.validationError).length > 0;
  }
  errorKeys(): string[] {
    return Object.keys(this.validationError);
  }

  checkRoomsState(): void {
    this.rxStompService.watch(`/topic/roomsState`).subscribe((message: any) => {
      this.zone.run(() => {
        const newRoom: roomDTO = JSON.parse(message.body);
        this.rooms.push(newRoom);
        this.cdr.detectChanges();
        console.log(this.rooms);
      });
    });
  }
  onSubmit(): void {}
}
