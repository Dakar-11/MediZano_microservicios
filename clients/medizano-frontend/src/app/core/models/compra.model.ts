export type TipoDocumentoCompra = 'FACTURA' | 'BOLETA' | 'GUIA_REMISION' | 'ORDEN_COMPRA';
export type EstadoCompra = 'BORRADOR' | 'PENDIENTE' | 'RECIBIDA' | 'CANCELADA';

export interface DetalleCompra {
  id?: number;
  productoId: number;
  nombreProducto: string;
  cantidadComprada: number;
  cantidadRecibida?: number;
  costoUnitario: number;
  descuento?: number;
  subtotal?: number;
  loteSugerido?: string;
  fechaVencimiento?: string;
}

export interface Compra {
  id: number;
  numeroDocumento: string;
  tipoDocumento: TipoDocumentoCompra;
  proveedorId: number;
  proveedorRuc: string;
  proveedorRazonSocial: string;
  proveedorTelefono?: string;
  fechaEmision: string;
  fechaRegistro: string;
  fechaRecepcion?: string;
  estado: EstadoCompra;
  subtotal: number;
  igv: number;
  descuento: number;
  total: number;
  observaciones?: string;
  usuarioRegistro?: string;
  usuarioRecepcion?: string;
  totalItems?: number;
  detalles: DetalleCompra[];
}

export interface CompraRequest {
  proveedorId: number;
  numeroDocumento: string;
  tipoDocumento: TipoDocumentoCompra;
  fechaEmision: string;
  observaciones?: string;
  detalles: {
    productoId: number;
    nombreProducto: string;
    cantidadComprada: number;
    costoUnitario: number;
    descuento?: number;
    loteSugerido?: string;
    fechaVencimiento?: string;
  }[];
}

export interface ItemRecepcion {
  detalleId: number;
  cantidadRecibida: number;
  batchNumber?: string;
  expiryDate?: string;
  purchasePrice?: number;
}

export interface RecepcionCompraRequest {
  usuarioRecepcion?: string;
  observaciones?: string;
  items: ItemRecepcion[];
}

export interface PaginatedCompras {
  content: Compra[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

