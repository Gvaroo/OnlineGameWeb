import { Component, HostListener, NgZone, OnDestroy } from '@angular/core';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { RxStompState } from '@stomp/rx-stomp';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { Subscription, first, interval, tap } from 'rxjs';
import { checkWinDTO } from 'src/app/models/Game/checkWinDTO';
import { gameDTO } from 'src/app/models/Game/gameDTO';
import { rematchDTO } from 'src/app/models/Game/rematchDTO';
import { userLeftDTO } from 'src/app/models/Game/userLeftDTO';
import { ServiceResponse } from 'src/app/models/serviceResponse';
import { RxStompService } from 'src/app/rx-stomp.service';
import { GameService } from 'src/app/services/game.service';

@Component({
  selector: 'app-oponnent',
  templateUrl: './oponnent.component.html',
  styleUrls: ['./oponnent.component.scss'],
})
export class OponnentComponent implements OnDestroy {
  roomId: string | null = null;
  roomSize: number = 0;
  role: string | null = null;
  gameBoard: string[][] = [];
  boardForPersistentState: string[][] = [];
  currentPlayerSymbol: string = 'O';
  winner: boolean = false;
  draw: boolean = false;
  isPlayerTurn: boolean = false;
  currentPlayer: string | null = null;
  opponentPlayer: string | null = null;
  timerSubscription2: any;
  timerSeconds: number = 30;
  remainingTime: number = this.timerSeconds;
  timerExpired: boolean = false;
  userLeft: boolean = false;
  remainingTimeForUserWhoLeft: number = 30;
  showModal: boolean = false;
  showRequestModal: boolean = false;
  rematchInfo!: rematchDTO;
  loading: boolean = false;
  timeout: boolean = false;
  gameOver: boolean = false;

  private topicSubscription: Subscription | undefined;
  constructor(
    private route: ActivatedRoute,
    private gameService: GameService,
    private _notification: NzNotificationService,
    private rxStompService: RxStompService,
    private router: Router
  ) {
    this.route.params.subscribe((params) => {
      this.roomId = params['roomUid'];
      this.getGameRoomInformation();
    });
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.userLeftGame();
      }
    });
  }
  ngOnDestroy(): void {
    this.stopTimer();
    if (this.topicSubscription) {
      this.topicSubscription.unsubscribe();
    }
  }
  @HostListener('window:beforeunload', ['$event'])
  unloadHandler(event: Event): void {
    this.userLeftGame();
  }
  ngOnInit(): void {
    if (this.timerExpired) {
      return;
    }
    this.timeoutRoomState();
    this.getOpponentState();
    this.rematchRequest();
    this.rematchState();
    this.closeRoomState();
    this.topicSubscription = this.rxStompService
      .watch(`/topic/${this.roomId}/test`)
      .subscribe((message: any) => {
        this.stopTimer();
        const currentGame: ServiceResponse<checkWinDTO> = JSON.parse(
          message.body
        );
        this.gameBoard = currentGame.data.board;
        if (
          currentGame.data.opponentLeft &&
          currentGame.data.currentPlayer == 'oponnent'
        ) {
          if (!this.checkWin(currentGame)) {
            this.startTimerIfOpponentLeft();
          }
        } else if (
          currentGame.data.opponentLeft &&
          currentGame.data.currentPlayer == 'creator'
        ) {
          if (!this.checkWin(currentGame)) {
            this.startTimerOfAbsentUser();
          }
        } else {
          this.userLeft = false;
          if (!this.checkWin(currentGame)) {
            this.isPlayerTurn = currentGame.data.currentPlayer == 'oponnent';
            if (this.isPlayerTurn) {
              this.remainingTime = this.timerSeconds;
              this.startTimer();
            }
          }
        }
      });
  }

  initializeGameBoard(size: number): void {
    this.gameBoard = Array.from({ length: size }, () => Array(size).fill(' '));
  }
  makeMove(row: number, col: number) {
    if (!this.isPlayerTurn) {
      this._notification.create(
        'error',
        'Status',
        "It's not your turn yet. Please wait for opponent to make turn"
      );
    } else if (this.gameBoard[row][col] !== ' ') {
      this._notification.create(
        'error',
        'Status',
        'There is symbol already there :) Please choose different cell'
      );
    } else {
      if (this.gameBoard[row][col] === ' ') {
        this.stopTimer();
        this.boardForPersistentState = this.gameBoard;
        this.boardForPersistentState[row][col] = this.currentPlayerSymbol;
        const moveRequest: gameDTO = {
          board: this.boardForPersistentState,
          playerSymbol: this.currentPlayerSymbol,
          row: row,
          col: col,
          roomUuid: this.roomId!,
          opponentLeft: false,
        };
        if (this.userLeft) moveRequest.opponentLeft = true;
        this.gameService.makeMove(moveRequest).subscribe((res) => {
          this.isPlayerTurn = false;
        });
      }
    }
  }
  startTimer() {
    this.remainingTime = this.timerSeconds;
    this.timerExpired = false;
    this.timerSubscription2 = interval(1000).subscribe(() => {
      this.remainingTime--;

      if (this.remainingTime === 0) {
        this.stopTimer();
        this._notification.create(
          'error',
          'Status',
          'Time has run out! The turn automatically goes to your opponent.'
        );
        const moveRequest: gameDTO = {
          board: this.gameBoard,
          playerSymbol: this.currentPlayerSymbol,
          roomUuid: this.roomId!,
        };
        this.gameService.makeMove(moveRequest).subscribe((res) => {
          this.isPlayerTurn = false;
        });
      }
    });
  }
  stopTimer() {
    if (this.timerSubscription2) {
      this.timerSubscription2.unsubscribe();
      this.timerSubscription2 = null;
      this.remainingTime = 0;
      this.isPlayerTurn = false;
      this.timerExpired = true;
    }
  }
  getGameRoomInformation(): void {
    this.stopTimer();
    this.isPlayerTurn = false;
    this.gameService
      .getGameRoom(this.roomId!, 'op')
      .pipe(
        tap(
          (res) => {
            console.log('GAME INFORMATION');
            console.log(res.data);
            this.roomSize = res.data.size;
            this.gameBoard = res.data.board;
            this.currentPlayer = res.data.opponentUsername;
            this.opponentPlayer = res.data.ownerUsername;
            if (res.data.currentSymbol != null) {
              this.isPlayerTurn =
                res.data.currentTurn == 'oponnent' &&
                res.data.currentTurn !== null;
            } else {
              if (!this.isPlayerTurn) {
                this._notification.create(
                  'success',
                  'Status',
                  "You've successfully joined room! Game starts now :)"
                );
              }
            }
            if (
              this.opponentPlayer != null &&
              this.isPlayerTurn &&
              res.data.timeLeft != null
            ) {
              this.startTimer();
              this.remainingTime = res.data.timeLeft;
            }
          },
          (err) => {
            this._notification.create(
              'error',
              'Status',
              'You should not be here'
            );
            this.router.navigateByUrl('/rooms');
          }
        )
      )
      .subscribe();
  }
  getOpponentState(): void {
    this.topicSubscription = this.rxStompService
      .watch(`/topic/${this.roomId}/userState/creator`)
      .subscribe((message: any) => {
        const opponentState: userLeftDTO = JSON.parse(message.body);
        console.log(opponentState);
        if (this.isPlayerTurn) this.userLeft = true;
        else {
          this.stopTimer();
          this.userLeft = true;

          this.remainingTimeForUserWhoLeft = opponentState.timeLeft!;
          this.timerSubscription2 = interval(1000).subscribe(() => {
            this.remainingTimeForUserWhoLeft--;
            if (this.remainingTimeForUserWhoLeft === 0) {
              this.stopTimer();
              this.remainingTimeForUserWhoLeft = this.timerSeconds;
              const moveRequest: gameDTO = {
                board: this.gameBoard,
                playerSymbol: 'X',
                roomUuid: this.roomId!,
                opponentLeft: true,
              };
              this.gameService.makeMove(moveRequest).subscribe((res) => {});
            }
          });
        }
      });
  }

  startTimerIfOpponentLeft(): void {
    this.isPlayerTurn = true;
    this.remainingTime = this.timerSeconds;
    this.timerSubscription2 = interval(1000).subscribe(() => {
      this.remainingTime--;
      if (this.remainingTime === 0) {
        this.stopTimer();
        this.remainingTime = this.timerSeconds;
        const moveRequest: gameDTO = {
          board: this.gameBoard,
          playerSymbol: 'O',
          roomUuid: this.roomId!,
          opponentLeft: true,
        };
        this.gameService.makeMove(moveRequest).subscribe((res) => {});
      }
    });
  }
  startTimerOfAbsentUser(): void {
    this.isPlayerTurn = false;
    var timer: number = 30;
    this.timerSubscription2 = interval(1000).subscribe(() => {
      timer--;
      if (timer === 0) {
        this.stopTimer();
        this.remainingTime = this.timerSeconds;
        const moveRequest: gameDTO = {
          board: this.gameBoard,
          playerSymbol: 'X',
          roomUuid: this.roomId!,
          opponentLeft: true,
        };
        this.gameService.makeMove(moveRequest).subscribe((res) => {});
      }
    });
  }
  checkWin(currentGame: ServiceResponse<checkWinDTO>): boolean {
    if (
      currentGame.data.winner &&
      currentGame.data.currentPlayer === 'creator'
    ) {
      this._notification.create(
        'success',
        'Status',
        'Congratulations you won:)'
      );
      this.winner = true;
      this.gameOver = true;
      this.openModal();
      return true;
    } else if (
      currentGame.data.winner &&
      currentGame.data.currentPlayer === 'oponnent'
    ) {
      this._notification.create('error', 'Status', 'Unfortunately you lost :)');
      this.openModal();
      this.gameOver = true;
      return true;
    } else if (currentGame.data.draw) {
      this._notification.create('info', 'Status', 'Its draw!');
      this.gameOver = true;
      this.openModal();
      return true;
    }
    return false;
  }
  openModal(): void {
    this.showModal = true;
  }
  closeModal(): void {
    this.showModal = false;
  }
  rematch() {
    this.loading = true;
    this.initializeGameBoard(this.roomSize);
    const rematch: rematchDTO = {
      roomId: this.roomId!,
      rematch: true,
      opponent: false,
      board: this.gameBoard,
      player: this.currentPlayer!,
    };
    const destination = `/app/topic/${this.roomId}/rematchRequest`;
    this.rxStompService.publish({
      destination: destination,
      body: JSON.stringify(rematch),
    });
    this.timerOfRematch();
  }

  closeRoom() {
    this.gameService.closeRoom(this.roomId!).subscribe((res) => {});
  }
  openRequestModal(): void {
    this.closeModal();
    this.loading = false;
    this.showRequestModal = true;
  }
  closeRequestModal(): void {
    this.showRequestModal = false;
  }

  rematchRequest(): void {
    this.topicSubscription = this.rxStompService
      .watch(`/topic/${this.roomId}/requestState`)
      .subscribe((message: any) => {
        this.rematchInfo = JSON.parse(message.body);
        if (this.rematchInfo.player != this.currentPlayer) {
          this.openRequestModal();
        }
      });
  }
  acceptRematch(): void {
    this.rematchInfo.opponent = true;
    this.gameService.rematchAccepted(this.rematchInfo).subscribe((res) => {});
  }
  rematchState(): void {
    this.topicSubscription = this.rxStompService
      .watch(`/topic/${this.roomId}/rematchState`)
      .subscribe((message: any) => {
        this.stopTimer();
        this.rematchInfo = JSON.parse(message.body);
        this.gameBoard = this.rematchInfo.board;
        this.winner = false;
        this.draw = false;
        this.timeout = false;
        this.gameOver = false;
        console.log(this.currentPlayer);
        this.isPlayerTurn = false;
        this.closeModal();
        this.closeRequestModal();
        this.loading = false;
        if (this.isPlayerTurn) this.startTimer();
        this._notification.create(
          'success',
          'Status',
          'Rematch started. Good luck :)'
        );
      });
  }

  closeRoomState(): void {
    this.topicSubscription = this.rxStompService
      .watch(`/topic/${this.roomId}/closingRoom`)
      .subscribe((message: any) => {
        const closeRoom: boolean = message.body;
        if (closeRoom) this.router.navigateByUrl('/rooms');
      });
  }
  timeoutRoomState(): void {
    this.topicSubscription = this.rxStompService
      .watch(`/topic/${this.roomId}/timeOut`)
      .subscribe((message: any) => {
        this.timeout = message.body;
        this.gameOver = true;
        this.stopTimer();
        this.openModal();
      });
  }
  timerOfRematch(): void {
    var timer: number = 120;
    this.timerSubscription2 = interval(1000).subscribe(() => {
      timer--;
      if (timer === 0) {
        this.stopTimer();
        this._notification.create(
          'info',
          'Status',
          '2 minutes have passed since you requested rematch. Closing room because of inactivity'
        );
        this.closeRoom();
      }
    });
  }
  userLeftGame(): void {
    const userLeft: userLeftDTO = {
      player: 'oponnent',
      roomUuid: this.roomId!,
    };
    if (this.gameOver) this.closeRoom();
    else {
      if (this.opponentPlayer != null) {
        if (this.remainingTime > 0) userLeft.timeLeft = this.remainingTime;
        else userLeft.timeLeft = 0;
        const formData = new FormData();
        formData.append('player', userLeft.player);
        formData.append('roomUuid', userLeft.roomUuid);
        formData.append('timeLeft', String(userLeft.timeLeft));

        navigator.sendBeacon(this.gameService.getUserLeftBoardUrl(), formData);
      }
    }
  }
}
