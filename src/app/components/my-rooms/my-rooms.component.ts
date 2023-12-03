import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { roomDTO } from 'src/app/models/Game/roomDTO';
import { GameService } from 'src/app/services/game.service';

@Component({
  selector: 'app-my-rooms',
  templateUrl: './my-rooms.component.html',
  styleUrls: ['./my-rooms.component.scss'],
})
export class MyRoomsComponent {
  rooms!: roomDTO[];
  constructor(private gameService: GameService, private router: Router) {
    this.getGameRooms();
  }

  getGameRooms(): void {
    this.gameService.getUserGameRooms().subscribe((res) => {
      this.rooms = res.data;
    });
  }
  joinRoom(roomId: number): void {
    this.gameService.rejoinRoom(roomId).subscribe((res) => {
      console.log(res.data);
      this.navigateToGameBoard(res.data.uuid, res.data.role);
    });
  }
  private navigateToGameBoard(uuid: string, role: string): void {
    const newUrl = `/${role}/${uuid}`;
    window.location.href = newUrl;
  }
}
