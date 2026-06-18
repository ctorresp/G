import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'shared-modal',
  imports: [],
  templateUrl: './modal.html',
  styleUrl: './modal.scss',
})
export class ModalComponent {
  @Input({ required: true }) title!: string;
  @Input() show: boolean = false;
  @Output() close = new EventEmitter<void>();

  onOverlayClick() {
    this.close.emit();
  }

  onContentClick(event: MouseEvent) {
    event.stopPropagation();
  }
}
