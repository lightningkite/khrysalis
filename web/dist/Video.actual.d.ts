import { Video } from './Video.shared';
import { Image } from './Image.shared';
import { Observable } from 'rxjs';
import { PointF } from './views/geometry/PointF.actual';
export declare function xVideoThumbnail(this_: Video, timeMs?: number, size?: (PointF | null)): Observable<Image>;
