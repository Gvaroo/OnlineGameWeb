import { Component, HostListener, NgZone, OnDestroy } from '@angular/core';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { Subscription, interval, tap } from 'rxjs';
import { checkWinDTO } from 'src/app/models/Game/checkWinDTO';
import { createOrJoinRoomDTO } from 'src/app/models/Game/createOrJoinRoomDTO';
import { gameDTO } from 'src/app/models/Game/gameDTO';
import { rematchDTO } from 'src/app/models/Game/rematchDTO';
import { userLeftDTO } from 'src/app/models/Game/userLeftDTO';
import { ServiceResponse } from 'src/app/models/serviceResponse';
import { RxStompService } from 'src/app/rx-stomp.service';
import { GameService } from 'src/app/services/game.service';

@Component({
  selector: 'app-creator',
  templateUrl: './creator.component.html',
  styleUrls: ['./creator.component.scss'],
})
export class CreatorComponent implements OnDestroy {
  roomId: string | null = null;
  roomSize: number = 0;
  loading: boolean = false;
  role: string | null = null;
  gameBoard: string[][] = [];
  boardForPersistentState: string[][] = [];
  currentPlayerSymbol: string = 'X';
  isPlayerTurn: boolean = true;
  currentPlayer: string | null = null;
  opponentPlayer: string | null = null;
  timerSubscription: any;
  timerSeconds: number = 30;
  remainingTime: number = this.timerSeconds;
  timerExpired: boolean = false;
  timeLeft: number = 0;
  userLeft: boolean = false;
  remainingTimeForUserWhoLeft: number = 30;
  showModal: boolean = false;
  showRequestModal: boolean = false;
  winner: boolean = false;
  gameOver: boolean = false;
  draw: boolean = false;
  rematchInfo!: rematchDTO;
  timeout: boolean = false;
  private topicSubscription: Subscription | undefined;
  shouldContinueListening = true;

  constructor(
    private route: ActivatedRoute,
    private gameService: GameService,
    private _notification: NzNotificationService,
    private rxStompService: RxStompService,
    private router: Router
  ) {
    this.isPlayerTurn = false;
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
    this.checkGameState();
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
          currentGame.data.currentPlayer == 'creator'
        ) {
          if (!this.checkWin(currentGame)) {
            this.startTimerIfOpponentLeft();
          }
        } else if (
          currentGame.data.opponentLeft &&
          currentGame.data.currentPlayer == 'oponnent'
        ) {
          if (!this.checkWin(currentGame)) {
            this.startTimerOfAbsentUser();
          }
        } else {
          this.userLeft = false;
          if (!this.checkWin(currentGame)) {
            this.isPlayerTurn = currentGame.data.currentPlayer == 'creator';
            if (this.isPlayerTurn) {
              this.remainingTime = this.timerSeconds;
              this.startTimer();
            }
          }
        }
      });
  }

  makeMove(row: number, col: number) {
    if (this.opponentPlayer == null) {
      this._notification.create(
        'error',
        'Status',
        'Please wait for opponent to join :)'
      );
    } else if (!this.isPlayerTurn) {
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

  initializeGameBoard(size: number): void {
    this.gameBoard = Array.from({ length: size }, () => Array(size).fill(' '));
  }
  startTimer() {
    this.timerExpired = false;
    this.remainingTime = this.timerSeconds;
    this.timerSubscription = interval(1000).subscribe(() => {
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
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
      this.remainingTime = 0;
      this.timerSubscription = null;
      this.isPlayerTurn = false;
      this.timerExpired = true;
    }
  }
  getOpponentState(): void {
    this.topicSubscription = this.rxStompService
      .watch(`/topic/${this.roomId}/userState/oponnent`)
      .subscribe((message: any) => {
        const opponentState: userLeftDTO = JSON.parse(message.body);
        console.log(opponentState);
        if (this.isPlayerTurn) this.userLeft = true;
        else {
          this.stopTimer();
          this.userLeft = true;
          this.remainingTimeForUserWhoLeft = opponentState.timeLeft!;
          this.timerSubscription = interval(1000).subscribe(() => {
            this.remainingTimeForUserWhoLeft--;
            if (this.remainingTimeForUserWhoLeft === 0) {
              this.stopTimer();
              this.remainingTimeForUserWhoLeft = this.timerSeconds;
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
      });
  }
  checkGameState(): void {
    this.topicSubscription = this.rxStompService
      .watch(`/topic/${this.roomId}/gameState`)
      .subscribe((message: any) => {
        if (!this.shouldContinueListening) {
          return; // Stop processing messages
        }
        const data: createOrJoinRoomDTO = JSON.parse(message.body);
        if (data.role == 'oponnentUser' && data.role != null) {
          this.isPlayerTurn = true;
          this.opponentPlayer = data.userName;
          console.log(this.opponentPlayer);
          this._notification.create(
            'success',
            'Status',
            `Opponent named '${data.userName}' joined your room. Game starts now!`
          );

          this.startTimer();
          this.gameService.getGameRoom(this.roomId!, 'ct').subscribe((res) => {
            this.currentPlayer = res.data.ownerUsername;
            this.opponentPlayer = res.data.opponentUsername;
          });
          this.shouldContinueListening = false;
        } else this.isPlayerTurn = false;
      });
  }
  getGameRoomInformation(): void {
    this.stopTimer();
    this.gameService
      .getGameRoom(this.roomId!, 'ct')
      .pipe(
        tap(
          (res) => {
            console.log('GAME INFORMATION');
            console.log(res.data);
            this.roomSize = res.data.size;
            this.gameBoard = res.data.board;
            this.currentPlayer = res.data.ownerUsername;
            this.opponentPlayer = res.data.opponentUsername;
            if (res.data.currentSymbol != null) {
              this.isPlayerTurn =
                res.data.currentTurn == 'creator' &&
                res.data.currentTurn !== null;
            } else {
              this._notification.create(
                'success',
                'Status',
                "You've successfully created room! Please wait for the opponent to join :)"
              );
            }

            if (
              this.opponentPlayer != null &&
              this.isPlayerTurn &&
              res.data.timeLeft != null
            ) {
              console.log('are u here?');
              console.log(res.data.timeLeft);
              this.startTimer();
              this.remainingTime = res.data.timeLeft;
            } else this.isPlayerTurn = false;
          },
          (err) => {
            this._notification.create('error', 'Status', 'Wrong url');
            this.router.navigateByUrl('/rooms');
          }
        )
      )
      .subscribe();
  }
  startTimerIfOpponentLeft(): void {
    this.isPlayerTurn = true;
    this.remainingTime = this.timerSeconds;
    this.timerSubscription = interval(1000).subscribe(() => {
      console.log(this.currentPlayerSymbol);
      this.remainingTime--;
      if (this.remainingTime === 0) {
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
  startTimerOfAbsentUser(): void {
    this.isPlayerTurn = false;
    var timer: number = 30;
    this.timerSubscription = interval(1000).subscribe(() => {
      console.log(this.currentPlayerSymbol);
      timer--;
      if (timer === 0) {
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

  checkWin(currentGame: ServiceResponse<checkWinDTO>): boolean {
    if (
      currentGame.data.winner &&
      currentGame.data.currentPlayer === 'oponnent'
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
      currentGame.data.currentPlayer === 'creator'
    ) {
      this._notification.create('error', 'Status', 'Unfortunately you lost :)');
      this.gameOver = true;
      this.openModal();
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
    console.log(this.currentPlayer);
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
        console.log(message.body);
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
        this.gameOver = false;
        this.draw = false;
        this.timeout = false;
        this.rematchInfo = JSON.parse(message.body);
        this.gameBoard = this.rematchInfo.board;
        this.winner = false;
        this.isPlayerTurn = true;
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
        this.openModal();
      });
  }

  timerOfRematch(): void {
    var timer: number = 120;
    this.timerSubscription = interval(1000).subscribe(() => {
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
      player: 'creator',
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
