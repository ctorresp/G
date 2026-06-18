import { Component, inject } from '@angular/core';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'shared-toast',
  imports: [],
  templateUrl: './toast.html',
  styleUrl: './toast.scss',
})
export class ToastComponent {
  protected readonly toastService = inject(ToastService);
}
