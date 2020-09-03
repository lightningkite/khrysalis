import { Image } from '../Image.shared';
import { DrawableResource } from "./DrawableResource";
import { Video } from "../Video.shared";
export declare function xImageViewLoadImage(this_: HTMLImageElement, image: (Image | null)): void;
export declare function imageViewSetImageResource(this_: HTMLImageElement, resource: DrawableResource): void;
export declare function xImageViewLoadVideoThumbnail(this_: HTMLImageElement, video: Video | null): void;
