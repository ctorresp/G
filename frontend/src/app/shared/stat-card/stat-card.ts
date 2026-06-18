import { Component, Input } from '@angular/core';

@Component({
  selector: 'shared-stat-card',
  imports: [],
  templateUrl: './stat-card.html',
  styleUrl: './stat-card.scss',
})
export class StatCardComponent {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) value!: number | string;
  @Input() icon: string = '';
  @Input() color: 'blue' | 'green' | 'red' | 'orange' | 'teal' = 'blue';
  @Input() clickable: boolean = false;
}
