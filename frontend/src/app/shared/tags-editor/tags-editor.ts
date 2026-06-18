import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'shared-tags-editor',
  imports: [FormsModule],
  templateUrl: './tags-editor.html',
  styleUrl: './tags-editor.scss',
})
export class TagsEditorComponent {
  @Input({ required: true }) tags: string[] = [];
  @Input() placeholder: string = 'Agregar...';
  @Input() label: string = '';
  @Output() add = new EventEmitter<string>();
  @Output() remove = new EventEmitter<number>();

  nuevoTag: string = '';

  agregar() {
    if (this.nuevoTag.trim()) {
      this.add.emit(this.nuevoTag.trim());
      this.nuevoTag = '';
    }
  }

  remover(index: number) {
    this.remove.emit(index);
  }
}
