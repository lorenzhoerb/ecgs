import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {UserService} from '../../services/user.service';
import {HttpResponse} from '@angular/common/http';
import LocalizationService, {LocalizeService} from '../../services/localization/localization.service';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';

@Component({
  selector: 'app-change-user-picture',
  templateUrl: './change-user-picture.component.html',
  styleUrls: ['./change-user-picture.component.scss']
})
export class ChangeUserPictureComponent implements OnInit {

  selectedFile: File;
  imageUrl: string | SafeUrl = '../../../assets/user_image.jpg';

  constructor(private router: Router,
              private notification: ToastrService,
              private userService: UserService,
              private sanitizer: DomSanitizer) {
  }

  public get localize(): LocalizeService {
    return LocalizationService;
  }

  ngOnInit(): void {
  }

  selectFile(event) {
    this.selectedFile = event.target.files[0];
    this.imageUrl = this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(event.target.files[0]));
  }

  uploadFile() {
    if (this.selectedFile != null) {
      if (this.selectedFile.size > 1000000) {
        this.notification.error('Datei größer als 1 Megabyte', 'Datei zu groß');
        return;
      }
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
