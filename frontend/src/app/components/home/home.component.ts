import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {CompetitionService} from '../../services/competition.service';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(public authService: AuthService, private notification: ToastrService) {
  }

  ngOnInit() {
  }
}
