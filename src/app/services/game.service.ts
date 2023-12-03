import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { gameDTO } from '../models/Game/gameDTO';
import { Observable } from 'rxjs';
import { LoginUserDTO } from '../models/User/LoginUserDTO';
import { createRoomDTO } from '../models/Game/createRoomDTO';
import { userLeftDTO } from '../models/Game/userLeftDTO';
import { rematchDTO } from '../models/Game/rematchDTO';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  makeMove(game: gameDTO): Observable<any> {
    console.log(game);
    return this.http.post<any>(`${this.apiUrl}game`, game, {
      withCredentials: true,
    });
  }
  // createGameRoom(size: number, gameBoard: string): Observable<any> {
  //   const params = new HttpParams().set('size', size.toString());
  //   return this.http.post<any>(
  //     `${this.apiUrl}createRoom`,
  //     { gameBoard },
  //     {
  //       params,
  //       withCredentials: true,
  //     }
  //   );
  // }
  createGameRoom(createRoom: createRoomDTO): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}createRoom`, createRoom, {
      withCredentials: true,
    });
  }
  testingClose(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}testingClose`, {
      withCredentials: true,
    });
  }
  userLeftBoard(dto: userLeftDTO): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}userLeft`, dto, {
      withCredentials: true,
    });
  }
  joinGameRoom(roomId: number): Observable<any> {
    const params = new HttpParams().set('roomId', roomId.toString());
    return this.http.post<any>(
      `${this.apiUrl}joinRoom`,
      {},
      {
        params,
        withCredentials: true,
      }
    );
  }
  getUserLeftBoardUrl(): string {
    return this.apiUrl + 'userLeft';
  }
  rematchAccepted(dto: rematchDTO): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}rematchAccepted`, dto, {
      withCredentials: true,
    });
  }
  closeRoom(uuid: string): Observable<any> {
    const params = new HttpParams().set('uuid', uuid);
    return this.http.get<any>(`${this.apiUrl}closeRoom`, {
      params,
      withCredentials: true,
    });
  }

  getGameRoom(roomUuid: string, ur: string): Observable<any> {
    const params = new HttpParams()
      .set('roomUuid', roomUuid.toString())
      .set('ur', ur);
    return this.http.get<any>(`${this.apiUrl}getRoom`, {
      params,
      withCredentials: true,
    });
  }
  getUserGameRooms(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}findUserRooms`, {
      withCredentials: true,
    });
  }
  rejoinRoom(roomId: number): Observable<any> {
    const params = new HttpParams().set('roomId', roomId);
    return this.http.get<any>(`${this.apiUrl}reconnectRoom`, {
      params,
      withCredentials: true,
    });
  }
  getGameRooms(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}getRooms`, {
      withCredentials: true,
    });
  }
  getLeaderBoard(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}public/getLeaderBoard`, {
      withCredentials: true,
    });
  }
}
