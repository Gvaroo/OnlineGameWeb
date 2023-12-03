export interface checkWinDTO {
  playerSymbol: string;
  row: number;
  col: number;
  gameOver: boolean;
  currentPlayer: string;
  username: string;
  winner: boolean;
  draw: boolean;
  opponentLeft?: boolean;
  board: string[][];
}
