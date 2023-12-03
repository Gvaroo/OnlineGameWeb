import { ProductDTO } from './ProductDTO';

export interface GetOrderDTO {
  orderId: number;
  products: ProductDTO[];
}
