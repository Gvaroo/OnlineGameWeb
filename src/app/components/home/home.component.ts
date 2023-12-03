import { Component, HostListener } from '@angular/core';
import { rankingDTO } from 'src/app/models/Game/rankingDTO';
import { GameService } from 'src/app/services/game.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  ranking!: rankingDTO[];
  constructor(private gameService: GameService) {
    this.getGameBoard();
  }

  public screenWidth: any;
  public screenHeight: any;
  receivedMessages: string[] = [];

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.screenWidth = window.innerWidth;
    this.screenHeight = window.innerHeight;
  }

  getGameBoard(): void {
    this.gameService.getLeaderBoard().subscribe((res) => {
      this.ranking = res.data;
    });
  }
}
