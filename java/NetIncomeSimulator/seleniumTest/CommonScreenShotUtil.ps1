# ref: https://www.it-swarm-ja.tech/ja/powershell/windows-powershell%E3%81%A7%E7%94%BB%E9%9D%A2%E3%82%AD%E3%83%A3%E3%83%97%E3%83%81%E3%83%A3%E3%82%92%E5%AE%9F%E8%A1%8C%E3%81%99%E3%82%8B%E3%81%AB%E3%81%AF%E3%81%A9%E3%81%86%E3%81%99%E3%82%8C%E3%81%B0%E3%82%88%E3%81%84%E3%81%A7%E3%81%99%E3%81%8B%EF%BC%9F/969655941/


class CommonScreenShotUtil {

    [String]$filePath

    CommonScreenShotUtil($filePrefix,$filePath) {

        $this.filePath = $filePath

    }

    [void] createScreenShot($filePrefix){

        $width = 0;
        $height = 0;
        $workingAreaX = 0;
        $workingAreaY = 0;

        $screen = [System.Windows.Forms.Screen]::AllScreens

        foreach ($item in $screen) {
            if ($workingAreaX -gt $item.WorkingArea.X) {
                $workingAreaX = $item.WorkingArea.X;
            }

            if ($workingAreaY -gt $item.WorkingArea.Y) {
                $workingAreaY = $item.WorkingArea.Y;
            }

            $width = $width + $item.Bounds.Width;

            if ($item.Bounds.Height -gt $height) {
                $height = $item.Bounds.Height;
            }
        }

        $bounds = [Drawing.Rectangle]::FromLTRB($workingAreaX, $workingAreaY, $width, $height); 
        $bmp = New-Object Drawing.Bitmap $width, $height;
        $graphics = [Drawing.Graphics]::FromImage($bmp);

        $graphics.CopyFromScreen($bounds.Location, [Drawing.Point]::Empty, $bounds.size);

        $bmp.Save((Join-Path $this.filePath ($filePrefix + (Get-Date).ToString("-yyyyMMdd-HHmmssfff") + '.bmp')));

        $graphics.Dispose();
        $bmp.Dispose();
    }
}