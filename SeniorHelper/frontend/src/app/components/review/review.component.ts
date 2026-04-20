import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ReviewService } from '../../services/review.service';

@Component({
  selector: 'app-review',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './review.component.html',
  styleUrls: ['./review.component.css']
})
export class ReviewComponent implements OnInit {

  reviews: any[] = [];

  newReview = {
    username: '',
    comment: '',
    rating: 1
  };

  constructor(private reviewService: ReviewService) {}

  ngOnInit(): void {
    this.loadReviews();
  }

  loadReviews(): void {
    this.reviewService.getReviews().subscribe(data => {
      this.reviews = data;
    });
  }

  submitReview(): void {
    this.reviewService.addReview(this.newReview).subscribe(() => {
      this.loadReviews();
      this.newReview = { username: '', comment: '', rating: 1 };
    });
  }
}
