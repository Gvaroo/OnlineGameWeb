import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'chunk',
})
export class ChunkPipe implements PipeTransform {
  transform(value: any[], size: number): any[] {
    if (!Array.isArray(value)) {
      return value;
    }

    const chunkedArray = [];
    for (let i = 0; i < value.length; i += size) {
      chunkedArray.push(value.slice(i, i + size));
    }

    return chunkedArray;
  }
}
