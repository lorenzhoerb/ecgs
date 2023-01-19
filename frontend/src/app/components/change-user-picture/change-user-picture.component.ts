import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../services/user.service';
import {HttpResponse} from '@angular/common/http';
import LocalizationService, {LocalizeService} from '../../services/localization/localization.service';

@Component({
  selector: 'app-change-user-picture',
  templateUrl: './change-user-picture.component.html',
  styleUrls: ['./change-user-picture.component.scss']
})
export class ChangeUserPictureComponent implements OnInit {

  selectedFile: File;

  constructor(private router: Router,
              private notification: ToastrService,
              private userService: UserService) {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
  }

  selectFile(event) {
    this.selectedFile = event.target.files[0];
  }

  uploadFile() {
    if (this.selectedFile != null) {
      const extension = this.selectedFile.name.substring(this.selectedFile.name.lastIndexOf('.'));
      if (extension === '.png' || extension === '.jpeg' || extension === '.jpg') {
        this.userService.uploadPicture(this.selectedFile).subscribe(
          event => {
            if (event instanceof HttpResponse) {
              this.notification.success('Bild erfolgreich hochgeladen.');
              this.userService.updateUserInfo();
              this.router.navigate(['']);
            }
          },
          (error) => {
            this.notification.error(error.error);
          }, () => {
          });
      } else {
        this.notification.error('Falscher Dateityp.');
      }
    } else {
      this.notification.error('Eine Datei muss ausgewählt sein.');
    }
  }
}
