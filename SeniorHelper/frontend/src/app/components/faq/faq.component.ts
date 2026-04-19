import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-faq',
  imports: [CommonModule],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.css',
})
export class FaqComponent {
  faqs = [
    { question: 'Can my family view my upcoming appointments?', answer: 'Yes, as long as they are listed as a caregiver attached to your account they will be able to view your account.' },
    { question: 'Where can I view my appointments? ', answer: 'You can view and create new appointments by clicking {calendar} in the top right corner.' },
    { question: 'How do I add my family / caregiver to my profile?', answer: 'In the top-right corner look for {connections}. From here you will be able to view current connections such as caregivers and add new ones as needed. ' },
    { question: 'Where do I see my education modules?', answer: 'You can view you education modules by clicking {education} in the top right corner of your screen. From there you can see all the new modules you can take, as well as others you’ve started or completed ' }
  ];

  getFormattedAnswer(answer: string): string {
  return answer
    .replace('{calendar}', '<span class="material-icons inline-icon">calendar_month</span>')
    .replace('{connections}', '<span class="material-icons inline-icon">groups</span>')
    .replace('{education}', '<span class="material-icons inline-icon">auto_stories</span>');
}

}

