export interface gameDTO {
  board: string[][];
  playerSymbol: string;
  row?: number;
  col?: number;
  roomUuid: string;
  opponentLeft?: boolean;
}
