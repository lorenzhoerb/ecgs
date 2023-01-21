export interface Pageable<T> {
  content: T[];
  pageable: {
    offset: number;
    pageNumber: number;
    pageSize: number;
    paged: boolean;
  };
  last: boolean;
  first: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
