export interface PaginatedResponse<T> {
  items: T[];
  currentPage: number;
  itemsPerPage: number;
  totalItems: number;
  totalPages: number;
}

export interface Media {
  id: number;
  title: string;
  overview: string;
  releaseDate: string
  posterUrl: string;
  genres: string[];
  externalRating: number;
  type: string;
}
