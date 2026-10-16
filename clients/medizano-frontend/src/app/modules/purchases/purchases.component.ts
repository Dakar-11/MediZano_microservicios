import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CompraService } from '../../core/services/compra.service';
import { ProveedorService } from '../../core/services/proveedor.service';
import { Compra, RecepcionCompraRequest, TipoDocumentoCompra } from '../../core/models/compra.model';
import { Proveedor } from '../../core/models/proveedor.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-purchases',
  templateUrl: './purchases.component.html',
  styleUrls: ['./purchases.component.scss'],
  standalone: false
})
export class PurchasesComponent implements OnInit {
  compras: Compra[] = [];
  proveedores: Proveedor[] = [];
  loading = false;
  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  // Search & Filter
  filtro = '';
  estadoFiltro = 'TODOS';

  // KPIs
  totalComprasRecibidas = 0;
  totalComprasPendientes = 0;
  montoTotalMes = 0;

  // Modals
  showNuevaModal = false;
  showRecepcionModal = false;
  showDetalleModal = false;

  selectedCompra?: Compra;
  compraForm!: FormGroup;
  recepcionForm!: FormGroup;

  errorMessage = '';
  successMessage = '';

  readonly tiposDocumento: TipoDocumentoCompra[] = ['FACTURA', 'BOLETA', 'GUIA_REMISION', 'ORDEN_COMPRA'];

  constructor(
    private compraService: CompraService,
    private proveedorService: ProveedorService,
    private fb: FormBuilder,
    public authService: AuthService
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.cargarProveedores();
    this.cargarCompras();
  }

  initForms(): void {
    this.compraForm = this.fb.group({
      proveedorId: [null, [Validators.required]],
      tipoDocumento: ['FACTURA', [Validators.required]],
      numeroDocumento: ['', [Validators.required, Validators.pattern(/^[A-Z0-9\-]{4,30}$/)]],
      fechaEmision: [new Date().toISOString().split('T')[0], [Validators.required]],
      observaciones: [''],
      detalles: this.fb.array([])
    });

    this.recepcionForm = this.fb.group({
      usuarioRecepcion: [this.authService.getCurrentUser()?.username || 'admin', [Validators.required]],
      observaciones: ['Mercadería recibida conforme en almacén FEFO'],
      items: this.fb.array([])
    });
  }

  get detallesFormArray(): FormArray {
    return this.compraForm.get('detalles') as FormArray;
  }

  get itemsRecepcionArray(): FormArray {
    return this.recepcionForm.get('items') as FormArray;
  }

  agregarDetalle(productoId: number = 1, nombre: string = 'Paracetamol 500mg Tab'): void {
    const item = this.fb.group({
      productoId: [productoId, [Validators.required]],
      nombreProducto: [nombre, [Validators.required]],
      cantidadComprada: [100, [Validators.required, Validators.min(1)]],
      costoUnitario: [1.20, [Validators.required, Validators.min(0.01)]],
      descuento: [0],
      loteSugerido: ['LOT-' + Math.floor(1000 + Math.random() * 9000)],
      fechaVencimiento: [new Date(Date.now() + 365*24*60*60*1000).toISOString().split('T')[0]]
    });
    this.detallesFormArray.push(item);
  }

  eliminarDetalle(index: number): void {
    this.detallesFormArray.removeAt(index);
  }

  calcularSubtotal(): number {
    return this.detallesFormArray.controls.reduce((acc, ctrl) => {
      const cant = ctrl.get('cantidadComprada')?.value || 0;
      const costo = ctrl.get('costoUnitario')?.value || 0;
      const desc = ctrl.get('descuento')?.value || 0;
      return acc + (cant * costo) - desc;
    }, 0);
  }

  cargarProveedores(): void {
    this.proveedorService.listarProveedores({ size: 100 }).subscribe({
      next: (resp) => {
        this.proveedores = resp.content;
      }
    });
  }

  cargarCompras(): void {
    this.loading = true;
    this.compraService.listarCompras(this.page, this.size, this.filtro, this.estadoFiltro).subscribe({
      next: (resp) => {
        this.compras = resp.content;
        this.totalPages = resp.totalPages;
        this.totalElements = resp.totalElements;
        this.calcularKpis();
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.message || 'Error al cargar compras';
        this.loading = false;
      }
    });
  }

  buscar(): void {
    this.page = 0;
    this.cargarCompras();
  }

  limpiar(): void {
    this.filtro = '';
    this.estadoFiltro = 'TODOS';
    this.page = 0;
    this.cargarCompras();
  }

  calcularKpis(): void {
    this.totalComprasRecibidas = this.compras.filter(c => c.estado === 'RECIBIDA').length;
    this.totalComprasPendientes = this.compras.filter(c => c.estado === 'PENDIENTE').length;
    this.montoTotalMes = this.compras.reduce((acc, c) => acc + (c.total || 0), 0);
  }

  abrirNuevaCompra(): void {
    this.compraForm.reset({
      proveedorId: this.proveedores.length > 0 ? this.proveedores[0].id : null,
      tipoDocumento: 'FACTURA',
      numeroDocumento: 'F001-' + Math.floor(100000 + Math.random() * 900000),
      fechaEmision: new Date().toISOString().split('T')[0]
    });
    this.detallesFormArray.clear();
    this.agregarDetalle(1, 'Paracetamol 500mg Tab');
    this.errorMessage = '';
    this.successMessage = '';
    this.showNuevaModal = true;
  }

  cerrarNuevaModal(): void {
    this.showNuevaModal = false;
  }

  guardarCompra(): void {
    const formVal = this.compraForm.value;
    const payload = {
      proveedorId: formVal.proveedorId,
      tipoDocumento: formVal.tipoDocumento,
      numeroDocumento: formVal.numeroDocumento,
      fechaEmision: formVal.fechaEmision,
      observaciones: formVal.observaciones,
      usuarioRegistro: this.authService.getCurrentUser()?.username || 'admin',
      items: (formVal.detalles || []).map((d: any) => ({
        productoId: d.productoId,
        nombreProducto: d.nombreProducto,
        cantidad: d.cantidadComprada,
        cantidadComprada: d.cantidadComprada,
        costoUnitario: d.costoUnitario,
        descuento: d.descuento || 0,
        loteSugerido: d.loteSugerido,
        fechaVencimiento: d.fechaVencimiento
      }))
    };

    this.loading = true;
    this.compraService.crearCompra(payload as any).subscribe({
      next: () => {
        this.successMessage = 'Orden de compra registrada exitosamente.';
        this.loading = false;
        this.cerrarNuevaModal();
        this.cargarCompras();
      },
      error: (err) => {
        this.errorMessage = err.message || 'Error al registrar orden de compra';
        this.loading = false;
      }
    });
  }

  abrirRecepcion(c: Compra): void {
    this.selectedCompra = c;
    this.itemsRecepcionArray.clear();
    this.recepcionForm.patchValue({
      usuarioRecepcion: this.authService.getCurrentUser()?.username || 'admin',
      observaciones: 'Mercadería recibida conforme e ingresada a almacén FEFO'
    });

    c.detalles.forEach(d => {
      this.itemsRecepcionArray.push(this.fb.group({
        detalleId: [d.id, [Validators.required]],
        nombreProducto: [d.nombreProducto],
        cantidadComprada: [d.cantidadComprada],
        cantidadRecibida: [d.cantidadComprada, [Validators.required, Validators.min(1)]],
        batchNumber: [d.loteSugerido || 'LOT-' + Math.floor(1000 + Math.random() * 9000), [Validators.required]],
        expiryDate: [d.fechaVencimiento || new Date(Date.now() + 365*24*60*60*1000).toISOString().split('T')[0], [Validators.required]],
        purchasePrice: [d.costoUnitario, [Validators.required]]
      }));
    });

    this.errorMessage = '';
    this.successMessage = '';
    this.showRecepcionModal = true;
  }

  cerrarRecepcionModal(): void {
    this.showRecepcionModal = false;
  }

  confirmarRecepcion(): void {
    if (this.recepcionForm.invalid || !this.selectedCompra) {
      return;
    }

    const payload: RecepcionCompraRequest = {
      usuarioRecepcion: this.recepcionForm.value.usuarioRecepcion,
      observaciones: this.recepcionForm.value.observaciones,
      items: this.itemsRecepcionArray.value.map((i: any) => ({
        detalleId: i.detalleId,
        cantidadRecibida: i.cantidadRecibida,
        batchNumber: i.batchNumber,
        expiryDate: i.expiryDate,
        purchasePrice: i.purchasePrice
      }))
    };

    this.loading = true;
    this.compraService.confirmarRecepcion(this.selectedCompra.id, payload).subscribe({
      next: () => {
        this.successMessage = `Recepción de compra ${this.selectedCompra?.numeroDocumento} procesada e inventario actualizado exitosamente con FEFO.`;
        this.loading = false;
        this.cerrarRecepcionModal();
        this.cargarCompras();
      },
      error: (err) => {
        this.errorMessage = err.message || 'Error al confirmar la recepción en inventario';
        this.loading = false;
      }
    });
  }

  verDetalles(c: Compra): void {
    this.selectedCompra = c;
    this.showDetalleModal = true;
  }

  cerrarDetalleModal(): void {
    this.showDetalleModal = false;
  }

  cancelar(c: Compra): void {
    if (!confirm(`¿Está seguro de cancelar la compra ${c.numeroDocumento}?`)) {
      return;
    }
    this.loading = true;
    this.compraService.cancelarCompra(c.id, 'Cancelado por usuario').subscribe({
      next: () => {
        this.successMessage = 'Compra cancelada correctamente.';
        this.cargarCompras();
      },
      error: (err) => {
        this.errorMessage = err.message || 'Error al cancelar compra';
        this.loading = false;
      }
    });
  }

  cambiarPagina(delta: number): void {
    const nueva = this.page + delta;
    if (nueva >= 0 && nueva < this.totalPages) {
      this.page = nueva;
      this.cargarCompras();
    }
  }
}
